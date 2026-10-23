# Lab 6: Docker Environment Variables and Resource Management

Source code: https://github.com/Ibrahim-Adel15/PythonApp

A small Flask application (port 5000). It reads `APP_NAME` and `APP_ENV` from the environment and refuses to start if either is missing. The lab shows three ways to pass environment variables to a container and how to limit its CPU and memory.

## Files

| File | Description |
|------|-------------|
| [`Dockerfile`](./Dockerfile) | Image `ivolve1`: `python:3.12-slim`, installs dependencies, runs on port 5000 |
| [`Dockerfile.hardcoded`](./Dockerfile.hardcoded) | Image `ivolve2`: same image with `APP_NAME` and `APP_ENV` hardcoded using `ENV` |
| [`env.txt`](./env.txt) | Environment variables file used with `--env-file` |
| [`app.py`](./app.py), [`requirements.txt`](./requirements.txt) | The application and its dependency (`Flask==3.1.2`) |

## Step 1: Dockerfile and image `ivolve1`

```dockerfile
FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app.py .
EXPOSE 5000
CMD ["python", "app.py"]
```

- `python:3.12-slim` is a small Python base image.
- `requirements.txt` is copied and installed **before** the application code, so Docker reuses the cached dependency layer when only `app.py` changes.
- `--no-cache-dir` keeps pip's download cache out of the image.

```bash
git clone https://github.com/Ibrahim-Adel15/PythonApp.git
cd PythonApp
docker build -t ivolve1 .
```

## Step 2: Run `container1` with environment variables and resource limits

```bash
docker run -d --name container1 -p 5000:5000 \
  -e APP_NAME=iVolveApp1 -e APP_ENV=stg \
  --cpus=1 --memory=256m ivolve1
```

| Option | Meaning |
|--------|---------|
| `-p 5000:5000` | Map host port 5000 to container port 5000 |
| `-e NAME=value` | Set an environment variable at run time |
| `--cpus=1` | Limit the container to 1 CPU |
| `--memory=256m` | Limit the container to 256MB of memory |

## Step 3: Verify the application, variables and limits

```bash
curl http://localhost:5000
docker exec container1 env | grep APP_
docker stats container1 --no-stream
docker inspect container1 --format 'CPU: {{.HostConfig.NanoCpus}} | Memory: {{.HostConfig.Memory}}'
```

Expected output of `curl`: a JSON response with `"application":"iVolveApp1"` and `"environment":"stg"`.

Result of `docker stats`:

```
CONTAINER ID   NAME         CPU %     MEM USAGE / LIMIT   MEM %     NET I/O        BLOCK I/O       PIDS
146731e3439a   container1   0.02%     21.3MiB / 256MiB    8.32%     1.8kB / 829B   262kB / 176kB   1
```

The memory limit (`256MiB`) is applied, and the application uses only about 21MiB. `docker stats` does not show the CPU limit, so it is checked with `docker inspect`: `NanoCpus` is `1000000000` for 1 CPU and `Memory` is `268435456` bytes (256MB).

## Step 4: Load variables from `env.txt` (`container2`)

```bash
printf 'APP_NAME=iVolveApp2\nAPP_ENV=prod\n' > env.txt
docker stop container1                     # port 5000 is already in use by container1
docker run -d --name container2 -p 5000:5000 --env-file env.txt ivolve1
curl http://localhost:5000
docker exec container2 env | grep APP_
```

Expected output: `iVolveApp2` and `prod`. `--env-file` loads every `KEY=value` line from the file, which is cleaner than many `-e` options and keeps values out of the shell history.

## Step 5: Hardcode the variables in the Dockerfile (`ivolve2`)

`Dockerfile.hardcoded` is the same as the first Dockerfile plus:

```dockerfile
ENV APP_NAME=iVolveApp3
ENV APP_ENV=dev
```

```bash
docker build -f Dockerfile.hardcoded -t ivolve2 .
docker rm -f container2                    # the name container2 is already used
docker run -d --name container2 -p 5000:5000 ivolve2
curl http://localhost:5000
docker exec container2 env | grep APP_
```

Result:

```
{"application":"iVolveApp3","environment":"dev","message":"Welcome to the Docker Env & Resource Management Lab!"}
APP_NAME=iVolveApp3
APP_ENV=dev
```

The values come from the image itself, so no `-e` or `--env-file` was needed.

## Comparison of the three methods

| Method | Where the value is set | Needs a new image to change? | Typical use |
|--------|------------------------|------------------------------|-------------|
| `-e` | `docker run` command | No | A few values, quick tests |
| `--env-file` | A file passed at run time | No | Many values, one file per environment |
| `ENV` in Dockerfile | Inside the image | Yes | Safe default values |

`ENV` values are only defaults: a value passed with `-e` or `--env-file` at run time overrides them. Secrets should not be hardcoded in a Dockerfile, because anyone with the image can read them (`docker inspect`).

## Cleanup

```bash
docker rm -f container1 container2
```

## Notes

- Port 5000 can be used by only one container at a time on the host, so `container1` had to be stopped before starting `container2` (alternatively map a different host port, for example `-p 5001:5000`).
- The application exits with an error if `APP_NAME` or `APP_ENV` is missing, which makes it easy to confirm the variables are really passed.
