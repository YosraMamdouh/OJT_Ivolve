# Lab 7: Multi-Network Microservices, Volumes, and Bind Mounts

Source code: https://github.com/Ibrahim-Adel15/MicroserviceApp

Three Flask microservices connected through two isolated Docker networks, with named volumes for logs and a bind mount for the backend data.

```
Browser ──8080──▶ frontend ──▶ backend ──▶ notification
                  (3000)      (5000)       (5001)
                  └─ frontend-backend ─┘└─ backend-notification ─┘
```

| Service | Port | Role |
|---------|-----:|------|
| `frontend` | 3000 | Web page to create and view messages (calls the backend) |
| `backend` | 5000 | Stores messages in `/app/data/messages.json`, logs to `/app/logs/backend.log`, sends notification requests |
| `notification` | 5001 | Records notifications in `/app/logs/notification.log` |

## Files

The folders `frontend/`, `backend/` and `notification/` are copied from the source repository. Each one contains `app.py`, `requirements.txt` and a `Dockerfile` (based on `python:3.12-slim`).

## Step 1: Build the images

```bash
git clone https://github.com/Ibrahim-Adel15/MicroserviceApp.git
cd MicroserviceApp

docker build -t frontend ./frontend
docker build -t backend ./backend
docker build -t notification ./notification
docker images
```

## Step 2: Create the two bridge networks with separate subnets

```bash
docker network create --driver bridge --subnet 10.10.1.0/24 frontend-backend
docker network create --driver bridge --subnet 10.10.2.0/24 backend-notification
docker network ls
```

| Network | Subnet | Connects |
|---------|--------|----------|
| `frontend-backend` | `10.10.1.0/24` | frontend and backend |
| `backend-notification` | `10.10.2.0/24` | backend and notification |

## Step 3: Create the volumes and the host directory

```bash
docker volume create backend-logs
docker volume create notification-logs
mkdir -p $HOME/backend-data
docker volume ls
```

| Storage | Type | Mounted in | Holds |
|---------|------|------------|-------|
| `backend-logs` | named volume | `backend:/app/logs` | `backend.log` |
| `notification-logs` | named volume | `notification:/app/logs` | `notification.log` |
| `$HOME/backend-data` | bind mount | `backend:/app/data` | `messages.json` |

## Step 4: Run the containers

**Notification** (on `backend-notification`):

```bash
docker run -d --name notification \
  --network backend-notification \
  -v notification-logs:/app/logs \
  notification
```

**Backend** (on both networks) with the notification URL:

```bash
docker run -d --name backend \
  --network frontend-backend \
  -e NOTIFICATION_URL=http://notification:5001/notify \
  -v backend-logs:/app/logs \
  -v $HOME/backend-data:/app/data \
  backend

docker network connect backend-notification backend
```

`docker run` can attach a container to one network only, so the second network is added with `docker network connect`.

**Frontend** (on `frontend-backend`, published on port 8080) with the backend URL:

```bash
docker run -d --name frontend \
  --network frontend-backend \
  -e BACKEND_URL=http://backend:5000 \
  -p 8080:3000 \
  frontend
```

The frontend listens on port 3000 inside the container, so host port 8080 is mapped to container port 3000. The container names (`backend`, `notification`) are used as host names because Docker provides DNS resolution by name on user-defined networks.

## Step 5: Verify the application

Open http://localhost:8080, create messages and refresh the page.

```bash
curl -I http://localhost:8080
docker ps
```

`curl` returned `HTTP/1.1 200 OK`.

**Messages were created and retrieved.** The messages `Hello Docker`, `hi`, `Learning microservices` and `bye` appear in the page.

**Bind mount** (data on the host):

```bash
cat $HOME/backend-data/messages.json
```

```json
[
  "Hello Docker",
  "hi",
  "Learning microservices"
]
```

**Backend log** (volume `backend-logs`):

```
Message created successfully: Hello Docker
Notification sent successfully
"POST /messages HTTP/1.1" 201
```

**Notification log** (volume `notification-logs`):

```
Notification received for message: Hello Docker
"POST /notify HTTP/1.1" 200
```

The requests to the backend come from `10.10.1.3` (frontend, network `frontend-backend`) and the requests to the notification service come from `10.10.2.3` (backend, network `backend-notification`), which shows that the backend is attached to both networks.

## Step 6: Test persistence

The backend container was removed and created again with the same volume and bind mount:

```bash
docker rm -f backend
docker run -d --name backend --network frontend-backend \
  -e NOTIFICATION_URL=http://notification:5001/notify \
  -v backend-logs:/app/logs -v $HOME/backend-data:/app/data backend
docker network connect backend-notification backend

curl -s http://localhost:8080 | grep "<li>"
curl -L -d "message=after restart" http://localhost:8080/send
docker exec notification tail -3 /app/logs/notification.log
```

Result: all old messages were still listed, the new message `after restart` was added, and the notification service logged `Notification received for message: after restart`. The data survived because it is stored outside the container (bind mount and volumes).

## Step 7: Test network isolation

The backend can reach the notification service:

```bash
docker exec backend python -c "import urllib.request; print(urllib.request.urlopen('http://notification:5001/health', timeout=3).read())"
# b'{"status":"healthy"}\n'
```

The frontend cannot, because it is not on the same network:

```bash
docker exec frontend python -c "
import urllib.request
try:
    urllib.request.urlopen('http://notification:5001/health', timeout=3)
except Exception as e:
    print('FAILED as expected:', e)
"
# FAILED as expected: <urlopen error [Errno -2] Name or service not known>
```

## Summary

| Requirement | Result |
|-------------|--------|
| Images for frontend, backend, notification | Built |
| Two bridge networks with separate subnets | `10.10.1.0/24` and `10.10.2.0/24` |
| Two volumes for backend and notification logs | `backend-logs`, `notification-logs` |
| Bind mount for backend data | `$HOME/backend-data` → `/app/data` |
| Backend on both networks with notification URL | Yes |
| Frontend on `frontend-backend`, port 8080, backend URL | Yes |
| Create and retrieve messages in the web page | Verified |

## Cleanup

```bash
docker rm -f frontend backend notification
docker network rm frontend-backend backend-notification
docker volume rm backend-logs notification-logs
```
