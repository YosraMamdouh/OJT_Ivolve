# Lab 4: Docker Image Optimization and Export/Load

Source code: https://github.com/Ibrahim-Adel15/ImageOptimization-

A Spring Boot web application (port 8080) that returns `Hello from Dockerized Spring Boot!`. The lab compares the original single-stage image with an optimized multi-stage image that runs as a non-root user, then exports and re-imports the optimized image as a TAR file.

## Files

| File | Used for |
|------|----------|
| [`Dockerfile`](./Dockerfile) | Original Dockerfile from the repository, builds `image1` |
| [`Dockerfile.optimized`](./Dockerfile.optimized) | Multi-stage, non-root Dockerfile, builds `image2` |
| `pom.xml`, `src/` | Application source (copied from the repository) |

The `lab-image.tar` file (82MB) is not committed to the repository because of its size.

## Step 1: Build `image1` from the existing Dockerfile

```bash
git clone https://github.com/Ibrahim-Adel15/ImageOptimization-.git
cd ImageOptimization-
docker build -t image1 .
docker images image1
```

The original Dockerfile uses a single stage based on `maven:3.9.15-eclipse-temurin-17-alpine`: it copies the whole project, runs `mvn package` and runs the JAR from the same image. Maven, the source code and the downloaded dependencies all stay in the final image, and there is no `USER` instruction, so the application runs as root.

## Step 2: Run and verify `container1`

```bash
docker run -d --name container1 -p 8080:8080 image1
curl http://localhost:8080
docker stop container1
docker rm container1
```

Expected output: `Hello from Dockerized Spring Boot!`

## Step 3: Multi-stage Dockerfile (non-root)

`Dockerfile.optimized`:

```dockerfile
# Stage 1: build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: run (JRE only, non-root)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

- **Stage 1 (`build`)** uses the Maven image to compile the application and produce the JAR. `pom.xml` is copied before `src` so Docker can reuse the cached dependency layer when only the code changes.
- **Stage 2** starts from a JRE-only Alpine image and copies just the finished JAR with `COPY --from=build`. Maven, the source code and the build cache are left behind in stage 1.
- `addgroup -S` and `adduser -S` create a system user and group (the Alpine syntax), and `USER appuser` makes the application run without root privileges.

## Step 4: Build `image2` and compare sizes

```bash
docker build -f Dockerfile.optimized -t image2 .
docker images
```

| Image | Dockerfile | Disk usage | Content size |
|-------|-----------|-----------:|-------------:|
| `image1` | Original (single-stage, root) | 671MB | 239MB |
| `image2` | Optimized (multi-stage, non-root) | 294MB | 85.7MB |

`image2` is about 377MB smaller by disk usage (roughly 56%), and about 64% smaller by content size.

## Step 5: Run `container2` and verify non-root

```bash
docker run -d --name container2 -p 8080:8080 image2
curl http://localhost:8080
docker exec container2 whoami
docker stop container2
docker rm container2
```

Results:
- The application responded with `Hello from Dockerized Spring Boot!`.
- `whoami` returned `appuser`, so the application is not running as root.

## Step 6: Export and load the image

```bash
docker save -o lab-image.tar image2
ls -lh lab-image.tar          # 82MB
docker rmi image2
docker images image2          # empty: the image was deleted
docker load -i lab-image.tar
docker images image2          # the image is back
```

Result: `docker load` printed `Loaded image: image2:latest`, and the restored image has the same ID as before (`90e60fbde780`), so the exact same image was restored from the TAR file.

## Why is `image2` smaller?

- **`image1`** keeps everything used during the build: Maven, a full JDK, the source code and the dependencies downloaded to `~/.m2`. None of this is needed to run the application.
- **`image2`** keeps only a JRE and the final JAR. Build tools stay in the discarded first stage.
- Running as `appuser` also reduces risk: if the application is compromised, the attacker does not get root inside the container.

## Notes

- `-DskipTests` is used in the build stage to speed up the build (the project has no tests).
- `docker save` / `docker load` are used to move an image between machines without a registry.
