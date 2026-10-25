# Lab 8: Containerized Node.js and MySQL Stack Using Docker Compose

Source code: https://github.com/Ibrahim-Adel15/iVolveApp

A Node.js (Express) web application that needs a MySQL database named `ivolve`. The stack runs with Docker Compose: an `app` service, a `db` service, a custom network, and volumes for the application logs and the database data. The application image is then tagged with a Docker Hub username and pushed.

## How the application works

- `server.js` listens on port 3000 and writes access logs to `/app/logs/access.log`.
- It connects to MySQL using `DB_HOST`, `DB_USER` and `DB_PASSWORD`, and **keeps retrying until a database named `ivolve` exists**, so the database must be created by the MySQL container.
- `GET /health` and `GET /ready` run `SELECT 1` against the database, so they confirm the connection to MySQL.

## Files

| File | Description |
|------|-------------|
| [`Dockerfile`](./Dockerfile) | Application image (`node:18-alpine`, installs dependencies, runs `node server.js`) |
| [`docker-compose.yml`](./docker-compose.yml) | The `app` and `db` services, the `ivolve-net` network and the volumes |
| [`.env.example`](./.env.example) | Template of the environment variables (copy it to `.env` and set real values) |
| [`.dockerignore`](./.dockerignore) | Keeps `.env`, `.git` and `node_modules` out of the image |
| `server.js`, `db.js`, `package.json`, `frontend/` | The application (copied from the source repository) |

## Step 1: Clone and prepare the environment file

```bash
git clone https://github.com/Ibrahim-Adel15/iVolveApp.git
cd iVolveApp

cat > .env <<'EOF'
MYSQL_ROOT_PASSWORD=RootPass123
DB_USER=ivolve_user
DB_PASSWORD=UserPass123
EOF

cat > .dockerignore <<'EOF'
.env
.git
node_modules
EOF

echo ".env" >> .gitignore
```

The values above are only for this lab. The real `.env` file is not committed (it is listed in `.gitignore`), and it is excluded from the image by `.dockerignore`, because the Dockerfile uses `COPY . .` and the image is pushed to Docker Hub.

## Step 2: `docker-compose.yml`

```yaml
services:
  app:
    build: .
    image: ivolve-app:v1
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      DB_NAME: ivolve
    volumes:
      - app_logs:/app/logs
    networks:
      - ivolve-net
    depends_on:
      db:
        condition: service_healthy

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ivolve
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - ivolve-net
    healthcheck:
      test: ["CMD-SHELL", "mysqladmin ping -h localhost -u root -p$$MYSQL_ROOT_PASSWORD"]
      interval: 5s
      timeout: 5s
      retries: 20

networks:
  ivolve-net:
    name: ivolve-net

volumes:
  app_logs:
  db_data:
```

| Requirement | How it is done |
|-------------|----------------|
| App built from the Dockerfile, port 3000 | `build: .` and `ports: "3000:3000"` |
| Database environment variables | `DB_HOST`, `DB_USER`, `DB_PASSWORD` on the app; `MYSQL_*` on the database |
| Database named `ivolve` | `MYSQL_DATABASE: ivolve` creates it on first start |
| Persist application logs | named volume `app_logs` at `/app/logs` |
| Persist database data | named volume `db_data` at `/var/lib/mysql` |
| Custom network `ivolve-net` | `networks.ivolve-net` with `name: ivolve-net`, used by both services |

The application reaches the database with the host name `db` (the service name), which Docker resolves on the `ivolve-net` network. The `healthcheck` and `depends_on` make the app start after MySQL reports healthy.

## Step 3: Start the services and verify

```bash
docker compose up -d --build
docker compose ps
docker compose logs app
```

```
NAME              IMAGE           COMMAND                  SERVICE   STATUS                   PORTS
ivolveapp-app-1   ivolve-app:v1   "docker-entrypoint.s…"   app       Up 2 minutes             0.0.0.0:3000->3000/tcp
ivolveapp-db-1    mysql:8.0       "docker-entrypoint.s…"   db        Up 2 minutes (healthy)   3306/tcp, 33060/tcp
```

Application log:

```
🔄 Attempting to connect to MySQL...
🔁 Retrying in 5 seconds...
❌ Connection error: connect ECONNREFUSED 172.18.0.2:3306
🔄 Attempting to connect to MySQL...
✅ Connected to MySQL and 'ivolve' DB found.
🚀 Server started on http://0.0.0.0:3000
```

The single `ECONNREFUSED` is expected: MySQL needs a few more seconds after the first start to open its port, and the application retries every 5 seconds until the connection succeeds.

**Application functionality and database connectivity:**

```bash
curl http://localhost:3000/health
curl http://localhost:3000/ready
docker compose exec db mysql -u ivolve_user -pUserPass123 -e "SHOW DATABASES;"
```

```
🚀 iVolve web app is working! Keep calm and code on! 🎉
👍 iVolve web app is ready to rock and roll! 🤘
```

```
+--------------------+
| Database           |
+--------------------+
| information_schema |
| ivolve             |
| performance_schema |
+--------------------+
```

`/health` and `/ready` run a query on the database, so a successful response proves the application is connected to MySQL. The `ivolve` database exists. The web page is available at http://localhost:3000.

**Network and volumes:**

```bash
docker network ls | grep ivolve-net
docker volume ls | grep -E "app_logs|db_data"
docker compose exec app cat /app/logs/access.log
```

## Step 4: Test persistence

```bash
docker compose down        # without -v, so the volumes are kept
docker compose up -d
docker compose ps
docker compose exec app cat /app/logs/access.log
```

The containers are removed and created again, but the volumes `app_logs` and `db_data` remain, so the old access log entries and the database are still there.

## Step 5: Build, tag and push the image to Docker Hub

Compose already tagged the image as `ivolve-app:v1` (`image:` in the compose file). It is tagged with the Docker Hub username and pushed:

```bash
export DH_USER=yosramamdouh234
docker login
docker tag ivolve-app:v1 $DH_USER/ivolve-app:v1
docker push $DH_USER/ivolve-app:v1
```

```
The push refers to repository [docker.io/yosramamdouh234/ivolve-app]
...
v1: digest: sha256:bbb2175676cbc57f218dadce995aef25a85a1879c3c40065a14439e2d8315640 size: 856
```

Image: `yosramamdouh234/ivolve-app:v1` → https://hub.docker.com/r/yosramamdouh234/ivolve-app

Anyone can run it with `docker pull yosramamdouh234/ivolve-app:v1` (it still needs a MySQL database named `ivolve` and the `DB_*` variables).

Before pushing, the image content was checked to make sure it does not contain the `.env` file:

```bash
docker run --rm --entrypoint sh ivolve-app:v1 -c "ls -a /app"
# .dockerignore  .gitignore  Dockerfile  db.js  docker-compose.yml  frontend
# node_modules  package-lock.json  package.json  server.js
```

## Cleanup

```bash
docker compose down -v     # -v also deletes the volumes (database and logs)
```
