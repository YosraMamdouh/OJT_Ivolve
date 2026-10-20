# Lab 3: Containerizing Java Application with Docker

This lab demonstrates how to containerize the Calculator Java application using Docker with a multi-stage build.

## Prerequisites
- Docker installed and running

## Steps to Build and Run

1. **Build the Docker Image (from the root directory):**
   ```bash
   docker build -t calculator-app:v1 -f Docker/Lab3/Dockerfile .
