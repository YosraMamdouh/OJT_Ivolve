# DevOps Labs

Hands-on labs from the iVolve DevOps training. The labs follow the path of an application: build it, package it in a container, secure and optimize the image, run it as a multi-container stack, and then run workloads on a Kubernetes cluster.

Each lab has its own folder with the files that were used and a README with the steps, the commands, the results and screenshots.

## Repository structure

```
DevOps-Labs
├── Build Tools
│   ├── Lab1   (Maven)
│   └── Lab2   (Gradle)
├── Docker
│   ├── Lab3   (Docker images for a Java app)
│   ├── Lab4   (Image optimization and export/load)
│   ├── Lab5   (Vulnerability scanning with Trivy)
│   ├── Lab6   (Environment variables and resource management)
│   ├── Lab7   (Multi-network microservices, volumes and bind mounts)
│   └── Lab8   (Node.js and MySQL stack with Docker Compose)
└── Kubernetes
    ├── Lab9   (Kubernetes cluster with kubeadm)
    ├── Lab10  (Node isolation with taints and labels)
    ├── Lab11  (Namespace and ResourceQuota)
    └── Lab12  (ConfigMap and Secret)
```

## Tools and technologies

| Area | Tools | Labs |
|------|-------|:----:|
| Build tools | Maven, Gradle (Java 17) | 1, 2 |
| Containers | Docker, multi-stage builds, non-root users, image export and load | 3, 4 |
| Security | Trivy image scanning and remediation | 5 |
| Configuration | Environment variables, `--env-file`, CPU and memory limits | 6 |
| Networking and storage | User-defined bridge networks, named volumes, bind mounts | 7 |
| Multi-container apps | Docker Compose, health checks, Docker Hub | 8 |
| Kubernetes cluster | kubeadm, kubelet, kubectl, containerd, Calico | 9 |
| Kubernetes scheduling and resources | Taints, labels, namespaces, ResourceQuota | 10, 11 |
| Kubernetes configuration | ConfigMap, Secret (Base64) | 12 |

## How to read this repository

1. Open the folder of a lab and read its `README.md`. It lists the files, the commands in the order they were run, the real output, and what each step does.
2. The `screenshots/` folder of each lab shows the commands and their results.
3. The files next to the README (Dockerfiles, `docker-compose.yml`, YAML manifests) are the ones used in the lab, so they can be reused.

## Prerequisites

The labs were done on Windows with **WSL2 (Ubuntu)**.

| Needed for | Requirement |
|------------|-------------|
| All labs | Git and a terminal (WSL2 with Ubuntu) |
| Labs 1 to 3 | Java 17, Maven and Gradle |
| Labs 3 to 8 | Docker and Docker Compose |
| Lab 5 | Trivy |
| Labs 9 to 12 | kubeadm, kubelet, kubectl and containerd (installed in Lab 9) |

## Labs

| Lab | Topic | Folder |
|:---:|-------|--------|
| 1 | Build and package a Java app with **Maven** | [Build Tools/Lab1](<./Build Tools/Lab1>) |
| 2 | Build and package a Java app with **Gradle** | [Build Tools/Lab2](<./Build Tools/Lab2>) |
| 3 | Create **Docker** images for a Java app (build inside Docker vs. build locally) | [Docker/Lab3](./Docker/Lab3) |
| 4 | Docker image optimization (multi-stage, non-root) and export/load | [Docker/Lab4](./Docker/Lab4) |
| 5 | Docker image vulnerability scanning and remediation with **Trivy** | [Docker/Lab5](./Docker/Lab5) |
| 6 | Docker environment variables and resource limits | [Docker/Lab6](./Docker/Lab6) |
| 7 | Multi-network microservices, volumes and bind mounts | [Docker/Lab7](./Docker/Lab7) |
| 8 | Containerized Node.js and MySQL stack with **Docker Compose** | [Docker/Lab8](./Docker/Lab8) |
| 9 | Deploy a **Kubernetes** cluster using **kubeadm** (containerd and Calico) | [Kubernetes/Lab9](./Kubernetes/Lab9) |
| 10 | Node isolation using **taints** and **labels** | [Kubernetes/Lab10](./Kubernetes/Lab10) |
| 11 | Namespace and **ResourceQuota** enforcement | [Kubernetes/Lab11](./Kubernetes/Lab11) |
| 12 | Configuration and sensitive data with **ConfigMap** and **Secret** | [Kubernetes/Lab12](./Kubernetes/Lab12) |

## Highlights

- **Lab 3:** building inside Docker produced an 814MB image, while building locally and copying the JAR produced a 427MB image.
- **Lab 4:** a multi-stage, non-root Dockerfile reduced the image from 671MB to 294MB.
- **Lab 5:** scanning with Trivy found thousands of vulnerabilities on an unsupported Debian-based image; updating the base image, Express and npm removed the HIGH and CRITICAL findings that have fixes available.
- **Lab 6:** environment variables were passed with `-e`, `--env-file` and `ENV`, and a container was limited to 1 CPU and 256MB of memory.
- **Lab 7:** three services on two isolated networks: the backend can reach the notification service, the frontend cannot. Messages and logs survive removing the container because they are stored in volumes and a bind mount.
- **Lab 8:** the app and MySQL start with one `docker compose up` command, logs and data persist in volumes, and the image is published as [`yosramamdouh234/ivolve-app:v1`](https://hub.docker.com/r/yosramamdouh234/ivolve-app) on Docker Hub.
- **Lab 9:** a Kubernetes cluster built with kubeadm, containerd and Calico. The node is `Ready` and all system pods are `Running`. It was built on a single node (control plane and worker together), so no separate worker joined the cluster.
- **Lab 10:** a node was tainted with `workload=ivolveApp:NoSchedule` and labeled `project=ivolve`, then checked with `kubectl describe node` and `kubectl get nodes --show-labels`.
- **Lab 11:** a ResourceQuota limits the `ivolve` namespace to 3 Pods, and the fourth Pod is rejected by Kubernetes.
- **Lab 12:** database settings are stored in a ConfigMap, and the passwords in a Secret with Base64-encoded values.

## Source code used

| Lab | Repository |
|:---:|------------|
| 1, 3 | https://github.com/Ibrahim-Adel15/CalculatorMavenApp |
| 2 | https://github.com/Ibrahim-Adel15/CalculatorGradleApp |
| 4 | https://github.com/Ibrahim-Adel15/ImageOptimization- |
| 5 | https://github.com/Ibrahim-Adel15/TrivyImage |
| 6 | https://github.com/Ibrahim-Adel15/PythonApp |
| 7 | https://github.com/Ibrahim-Adel15/MicroserviceApp |
| 8 | https://github.com/Ibrahim-Adel15/iVolveApp |

Each lab folder contains the files used in that lab, copied from these repositories. Labs 9 to 12 are done with commands and manifests written for the lab, so they have no source repository.

> Lab 8 reads its passwords from a `.env` file, which is not committed. Copy `.env.example` to `.env` and set your own values.
