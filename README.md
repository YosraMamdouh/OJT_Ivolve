# DevOps Labs

Hands-on labs for building, packaging and containerizing applications.

## Repository structure

```
DevOps-Labs
├── Build Tools
│   ├── Lab1   (Maven)
│   └── Lab2   (Gradle)
└── Docker
    ├── Lab3   (Docker images for a Java app)
    ├── Lab4   (Image optimization and export/load)
    ├── Lab5   (Vulnerability scanning with Trivy)
    └── Lab6   (Environment variables and resource management)
```

## Labs

| Lab | Topic | Folder |
|-----|-------|--------|
| 1 | Build and package a Java app with **Maven** | [Build Tools/Lab1](<./Build Tools/Lab1>) |
| 2 | Build and package a Java app with **Gradle** | [Build Tools/Lab2](<./Build Tools/Lab2>) |
| 3 | Create **Docker** images for a Java app (build inside Docker vs. build locally) | [Docker/Lab3](./Docker/Lab3) |
| 4 | Docker image optimization (multi-stage, non-root) and export/load | [Docker/Lab4](./Docker/Lab4) |
| 5 | Docker image vulnerability scanning and remediation with **Trivy** | [Docker/Lab5](./Docker/Lab5) |
| 6 | Docker environment variables and resource limits | [Docker/Lab6](./Docker/Lab6) |

Each lab folder has its own README with the steps, commands and results.

## Highlights

- **Lab 3:** building inside Docker produced an 814MB image, while building locally and copying the JAR produced a 427MB image.
- **Lab 4:** a multi-stage, non-root Dockerfile reduced the image from 671MB to 294MB.
- **Lab 5:** scanning with Trivy found thousands of vulnerabilities on an unsupported Debian-based image; updating the base image, Express and npm removed the HIGH and CRITICAL findings that have fixes available.
- **Lab 6:** environment variables were passed with `-e`, `--env-file` and `ENV`, and a container was limited to 1 CPU and 256MB of memory.

## Source code used

- Maven app: https://github.com/Ibrahim-Adel15/CalculatorMavenApp
- Gradle app: https://github.com/Ibrahim-Adel15/CalculatorGradleApp
- Image optimization app: https://github.com/Ibrahim-Adel15/ImageOptimization-
- Trivy lab app: https://github.com/Ibrahim-Adel15/TrivyImage
- Python app: https://github.com/Ibrahim-Adel15/PythonApp

Each lab folder contains the files used in that lab, copied from these repositories.
