# Lab 3: Creating a Docker Image for a Java Application

This lab explores two different approaches for containerizing a Java Maven application, focusing on security (non-root user), image sizes, and multi-stage builds.

---

## Part 1: Build Inside Docker

1. **Build the Image (`app1`):**
   ```bash
   docker build -t app1 -f Docker/Lab3/Dockerfile.part1 .
