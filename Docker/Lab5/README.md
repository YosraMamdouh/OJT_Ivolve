# Lab 5: Docker Image Vulnerability Scanning with Trivy

Source code: https://github.com/Ibrahim-Adel15/TrivyImage

A small Node.js (Express) application (port 3000). The lab builds an image from the provided Dockerfile, scans it with [Trivy](https://trivy.dev), and then fixes the HIGH and CRITICAL vulnerabilities by updating the base image and the dependencies.

## Files

| File | Description |
|------|-------------|
| [`Dockerfile.v1`](./Dockerfile.v1) | Original Dockerfile (`node:18-bullseye`) |
| [`package.v1.json`](./package.v1.json) | Original dependencies (`express 4.18.2`) |
| [`Dockerfile`](./Dockerfile) | Fixed Dockerfile (v2) |
| [`package.json`](./package.json) | Updated dependencies (v2) |
| [`app.js`](./app.js) | The application |
| [`report-v1.html`](./report-v1.html) | Trivy HTML report for `trivy-lab:v1` (HIGH and CRITICAL) |
| [`report-v2.html`](./report-v2.html) | Trivy HTML report for `ivolve-app:v2` after remediation |

## Step 1: Build the image

```bash
git clone https://github.com/Ibrahim-Adel15/TrivyImage.git
cd TrivyImage
docker build -t trivy-lab:v1 .
```

## Step 2: Install Trivy

```bash
sudo apt-get install -y wget gnupg lsb-release apt-transport-https
wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | gpg --dearmor | sudo tee /usr/share/keyrings/trivy.gpg > /dev/null
echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb generic main" | sudo tee /etc/apt/sources.list.d/trivy.list
sudo apt-get update
sudo apt-get install -y trivy
trivy --version
```

## Step 3: Scan the image

```bash
trivy image trivy-lab:v1                                    # all severities
trivy image --severity HIGH,CRITICAL trivy-lab:v1           # HIGH and CRITICAL only
```

## Step 4: Generate the HTML report

```bash
trivy image --severity HIGH,CRITICAL --format template \
  --template "@/usr/local/share/trivy/templates/html.tpl" \
  -o report-v1.html trivy-lab:v1
```

### Scan results for `trivy-lab:v1`

| Target | Total | HIGH | CRITICAL |
|--------|------:|-----:|---------:|
| Operating system packages | 2347 | 2128 | 219 |
| Node.js packages (`package.json`) | 27 | 26 | 1 |

Trivy also warned: `This OS version is no longer supported by the distribution (debian 11.11)`, meaning the base image no longer receives security updates.

## Bonus: Vulnerability remediation

### Investigation

- **Operating system (2347 findings):** the base image `node:18-bullseye` is a full Debian 11 image with about 409 packages. Debian 11 is no longer supported, so security fixes are not published for it, and Node 18 itself is end-of-life.
- **Application dependencies:** `express 4.18.2` pulls in `body-parser 1.20.1` and `path-to-regexp 0.1.7`, both with known HIGH vulnerabilities (ReDoS and denial of service).
- **npm itself:** the Node image bundles npm, which brings its own vulnerable packages (`brace-expansion`, `ip-address`, `pacote`, `picomatch`, `sigstore`).

### Fixes

1. **Base image:** `node:18-bullseye` → `node:22-alpine` (supported Node version on a minimal Alpine base, with far fewer packages installed).
2. **Application dependency:** updated `express` in `package.json` to the latest version.
3. **npm:** added `RUN npm install -g npm@latest` to update the npm bundled in the image.

Fixed `Dockerfile` (v2):

```dockerfile
FROM node:22-alpine
RUN npm install -g npm@latest
WORKDIR /app
COPY package.json ./
RUN npm install
COPY app.js ./
EXPOSE 3000
CMD ["npm", "start"]
```

### Rebuild and rescan

```bash
docker build --no-cache -t ivolve-app:v2 .
trivy image --severity HIGH,CRITICAL --ignore-unfixed ivolve-app:v2
trivy image --severity HIGH,CRITICAL --ignore-unfixed --format template \
  --template "@/usr/local/share/trivy/templates/html.tpl" \
  -o report-v2.html ivolve-app:v2
```

`--ignore-unfixed` hides vulnerabilities that have no fix available yet, matching the requirement *"no HIGH or CRITICAL remain, where fixes are available"*. `--no-cache` makes sure the updated npm is really installed.

### Results

| Image | Scan | HIGH | CRITICAL |
|-------|------|-----:|---------:|
| `trivy-lab:v1` | OS packages | 2128 | 219 |
| `trivy-lab:v1` | Node.js packages | 26 | 1 |
| `ivolve-app:v2` (after changing the base image only) | Node.js packages, fixed only | 14 | 0 |
| `ivolve-app:v2` (final) | all, fixed only | 0 | 0 |

After the base image change, the 14 remaining findings were all in Express dependencies (`body-parser`, `path-to-regexp`) and in the packages bundled with npm. Updating Express and npm removed them. The application still works on the new image:

```bash
docker run -d --name t2 -p 3000:3000 ivolve-app:v2
curl http://localhost:3000          # Welcome to the Trivy Security Lab!
curl http://localhost:3000/health   # {"status":"UP"}
docker rm -f t2
```

Note: the v1 numbers include vulnerabilities without a fix (the scan ran without `--ignore-unfixed`), while the v2 scan ignores unfixed ones, as the lab requires. The huge drop is still mainly caused by leaving an unsupported, full Debian base image.

## Lessons

- Keep base images supported, minimal and up to date (Alpine or slim variants instead of full images).
- Scan images regularly, and read the *Fixed Version* column to decide what to update.
- Rebuild with `--no-cache` after updating dependencies so old cached layers are not reused.
