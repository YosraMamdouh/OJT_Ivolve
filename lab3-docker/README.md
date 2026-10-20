# Lab 3: Creating a Docker Image for a Java Application

Source code: https://github.com/Ibrahim-Adel15/CalculatorMavenApp

Two approaches are compared: building the app **inside** Docker, and building it **locally** and copying the JAR into a runtime image.

## Files

| File | Used for |
|------|----------|
| [`Dockerfile`](./Dockerfile) | Part 1: build inside Docker (Maven image, Java 17) |
| [`Dockerfile.local`](./Dockerfile.local) | Part 2: copy the locally built JAR into a Java 17 JRE image |

Both Dockerfiles must be built from the root of the cloned `CalculatorMavenApp` project, so copy them there first:

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp
cp /path/to/this/repo/lab3-docker/Dockerfile .
cp /path/to/this/repo/lab3-docker/Dockerfile.local .
```

> The app is a console application that reads numbers from standard input. It does not listen on a port, so `EXPOSE 8080` is only metadata required by the lab. Containers are run with `-it` so the app can read input.

## Part 1: Build inside Docker

The Dockerfile uses `maven:3.9-eclipse-temurin-17`, creates a working directory (`/app`) and a non-root user (`appuser`), copies `pom.xml` and `src`, builds with `mvn package`, and runs the generated JAR.

```bash
docker build -t app1 .
docker images app1
docker run -it --name container1 app1
docker rm container1
docker rmi app1
```

Result: the application printed the expected output for inputs `10` and `5`.

The container prints a harmless warning (`mkdir: cannot create directory '/root'`): the Maven image entrypoint tries to write to `/root/.m2`, which the non-root user cannot access. The application still runs.

## Part 2: Build locally

Build the JAR on the host first:

```bash
mvn package
```

Then build the image from a Java 17 runtime (JRE) base image that only copies the generated JAR:

```bash
docker build -f Dockerfile.local -t app2 .
docker images app2
docker run -it --name container2 app2
docker rm container2
docker rmi app2
```

Result: the application printed the expected output for inputs `10` and `5`.

## Bonus: image size comparison

| Image | Approach | Disk usage | Content size |
|-------|----------|-----------:|-------------:|
| `app1` | Build inside Docker | 814MB | 251MB |
| `app2` | Build locally | 427MB | 110MB |

`app2` is about 387MB (roughly 47%) smaller by disk usage.

### Why the difference?

- **`app1`** starts from the Maven image, which contains Maven and a full JDK (with the compiler). The image also holds the source code, the downloaded dependencies (`~/.m2`) and intermediate build files. None of this is needed to run the app.
- **`app2`** starts from a JRE-only image and only adds the finished `calculator.jar`.

### Trade-offs

- **Build inside Docker:** no Java or Maven needed on the host and the build is reproducible, but the image is large and ships build tools it does not need.
- **Build locally:** a much smaller and faster image, but it depends on Java and Maven being installed on the host in the right version.

A **multi-stage build** combines both advantages: build the JAR in a Maven stage, then copy only the JAR into a JRE stage.
