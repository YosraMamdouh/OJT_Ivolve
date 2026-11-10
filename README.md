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
├── Kubernetes
│   ├── Lab9   (Kubernetes cluster with kubeadm)
│   ├── Lab10  (Node isolation with taints and labels)
│   ├── Lab11  (Namespace and ResourceQuota)
│   ├── Lab12  (ConfigMap and Secret)
│   ├── Lab13  (Persistent storage with PV and PVC)
│   ├── Lab14  (StatefulSet with a headless Service)
│   ├── Lab15  (Node.js application with a ClusterIP Service)
│   ├── Lab16  (Resource requests and limits)
│   ├── Lab17  (Network Policy)
│   ├── Lab18  (Monitoring with Prometheus, Alertmanager, Grafana and a DaemonSet)
│   └── Lab19  (RBAC and Service Accounts)
└── jenkins
    ├── Lab20  (Role-based authorization in Jenkins)
    ├── Lab21  (Jenkins pipeline: build, scan, push and deploy)
    ├── Lab22  (Shared libraries and a Jenkins agent)
    └── Lab23  (GitOps with Jenkins and Argo CD)
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
| Kubernetes storage | PersistentVolume, PersistentVolumeClaim, `hostPath` | 13 |
| Kubernetes stateful workloads | StatefulSet, headless Service, PVC template, cluster DNS, MySQL | 14 |
| Kubernetes application deployment | Deployment, init container, ClusterIP Service, node selector, tolerations | 15 |
| Kubernetes resource management | Requests and limits, metrics server, `kubectl top` | 16 |
| Kubernetes networking | NetworkPolicy (Calico) | 17 |
| Kubernetes monitoring | DaemonSet, Prometheus, Alertmanager (e-mail alerts), Grafana, node-exporter | 18 |
| Kubernetes security | ServiceAccount, token, Role, RoleBinding, RBAC | 19 |
| CI/CD security | Jenkins, Role-based Authorization Strategy plugin | 20 |

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
| Labs 9 to 19 | kubeadm, kubelet, kubectl and containerd (installed in Lab 9) |
| Lab 20, 21, 22, 23 | Jenkins (run in a Docker container), Jenkinsfile pipeline, shared libraries, Jenkins agent, Trivy, Docker Hub, Argo CD (GitOps) |

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
| 13 | Persistent storage with a **PersistentVolume** and a **PersistentVolumeClaim** | [Kubernetes/Lab13](./Kubernetes/Lab13) |
| 14 | MySQL as a **StatefulSet** with a **headless Service** | [Kubernetes/Lab14](./Kubernetes/Lab14) |
| 15 | Node.js application **Deployment** with an init container and a **ClusterIP Service** | [Kubernetes/Lab15](./Kubernetes/Lab15) |
| 16 | Pod resource management with CPU and memory **requests and limits** | [Kubernetes/Lab16](./Kubernetes/Lab16) |
| 17 | Control Pod-to-Pod traffic with a **Network Policy** | [Kubernetes/Lab17](./Kubernetes/Lab17) |
| 18 | Node-wide monitoring with a **DaemonSet**, Prometheus, Alertmanager and Grafana | [Kubernetes/Lab18](./Kubernetes/Lab18) |
| 19 | Secure Kubernetes with **RBAC** and a **Service Account** | [Kubernetes/Lab19](./Kubernetes/Lab19) |
| 20 | **Role-based authorization** in Jenkins (admin and read-only users) | [jenkins/Lab20](./jenkins/Lab20) |
| 21 | **Jenkins pipeline** that builds, scans, pushes and deploys the application to Kubernetes | [jenkins/Lab21](./jenkins/Lab21) |
| 22 | Pipeline with **Shared Libraries** (BuildApp, BuildImage, DeployOnK8s) running on a **Jenkins agent** | [jenkins/Lab22](./jenkins/Lab22) |
| 23 | **GitOps** with Jenkins and **Argo CD**: Jenkins pushes the edited deployment file to GitHub and Argo CD deploys it | [jenkins/Lab23](./jenkins/Lab23) |

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
- **Lab 13:** a 1Gi `hostPath` PersistentVolume (`ReadWriteMany`, `Retain`) bound to a PersistentVolumeClaim without any StorageClass. A test Pod wrote a log file that was read both inside the Pod and on the node.
- **Lab 14:** MySQL runs as a StatefulSet that reads its settings from the ConfigMap and the Secret, runs only on the node labeled `project=ivolve`, and stores its data on a PersistentVolume. A client connection works, and the headless Service name and the Pod name both resolve to the Pod IP.
- **Lab 15:** the image from Lab 8 runs as a Deployment of 2 replicas. An init container with a MySQL client creates the `ivolve` database and grants the privileges, the Pods store their logs on the volume of Lab 13, and a ClusterIP Service distributes the traffic. Both Pods connect to MySQL and find the `ivolve` database.
- **Lab 16:** the Deployment was updated with requests (1 CPU, 1Gi) and limits (2 CPUs, 2Gi), which were verified with `kubectl describe pod`. The metrics server was installed, and `kubectl top` shows that the application Pods use about 20Mi of memory, far below their requests.
- **Lab 17:** a Network Policy allows only the application Pods to reach MySQL on port 3306. A test Pod could not connect without the application label (the connection timed out), and could connect after the label was added.
- **Lab 18:** a DaemonSet runs `node-exporter` on every node and exposes metrics on port 9100. Prometheus scrapes them, Alertmanager sends an e-mail when CPU or memory usage is above 80% for 1 minute, and Grafana shows a provisioned CPU and memory dashboard. The SMTP password is kept in a Kubernetes Secret and is not stored in the repository.
- **Lab 19:** the Service Account `jenkins-sa` can create and update Deployments in the `ivolve` namespace only. It was tested with its own token: creating and replacing a Deployment worked, while reading or deleting Deployments, creating Deployments in `default` and `kube-system`, and listing Pods were forbidden.
- **Lab 20:** Jenkins got an `admin` role and a `readonly` role with the Role-based Authorization Strategy plugin. `user1` is an administrator, and `user2` can see the jobs but does not get New Item, Manage Jenkins or Build Now.
- **Lab 21:** a Jenkins pipeline (`Jenkinsfile`) with 7 stages: build the app, build the Docker image, scan it with Trivy, push it to Docker Hub, delete it locally, write the new tag into the deployment file, and deploy to Kubernetes with the `jenkins-sa` token of Lab 19. The image tag is the build number, and the Deployment rolled out to `yosramamdouh234/ivolve-app:3`. The post actions `always`, `success` and `failure` clean up the image and the Docker login and print the result.
- **Lab 22:** the stages of the pipeline moved into three shared libraries (`buildApp`, `buildImage`, `deployOnK8s`) kept in their own repository, and the `Jenkinsfile` only calls them. The pipeline runs on a Jenkins agent (`docker-agent`, a Docker container connected to Jenkins over WebSocket). Build #5 ran on the agent, pushed `ivolve-app:5` and deployed it, and both application Pods were `Running`.
- **Lab 23:** Argo CD was installed in the `argocd` namespace (with a toleration for the tainted node) and an Application named `ivolve` watches the `k8s/` folder of the application repository with automatic sync. The `deployOnK8s` library now edits the deployment file and pushes it to GitHub instead of calling `kubectl`. After build #6, GitHub had the commit `Update image to 6`, and the Deployment in the cluster ran `ivolve-app:6` without Jenkins touching the cluster.

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
| 21, 22, 23 | https://github.com/YosraMamdouh/Ivolve-app-cicd (the application of Lab 8 with its Dockerfile, Kubernetes files and Jenkinsfile) |
| 22, 23 | https://github.com/YosraMamdouh/ivolve-shared-library (the shared libraries) |

Each lab folder contains the files used in that lab, copied from these repositories. Labs 9 to 20 are done with commands, manifests and settings written for the lab, so they have no source repository.

> In the Kubernetes labs the task names such as `ivolveQuota` or `ivolveDB` contain capital letters, which Kubernetes does not accept, so the objects use lowercase names with hyphens (`ivolve-quota`, `ivolve-db`, and so on).

> Lab 8 reads its passwords from a `.env` file, which is not committed. Copy `.env.example` to `.env` and set your own values.
