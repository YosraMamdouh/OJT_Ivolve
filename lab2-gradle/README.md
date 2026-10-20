# Lab 2: Building and Packaging a Java Application with Gradle

Source code: https://github.com/Ibrahim-Adel15/CalculatorGradleApp

> Note: the lab title mentions a Go application, but the provided project is a **Java** application built with Gradle (`build.gradle`, Java 17 toolchain).

## Goal

Install Gradle, run the unit tests, build the application into `build/libs/calculator.jar`, run it and verify it works.

## Steps

### 1. Install Gradle

Gradle is installed with SDKMAN (Java 17 must already be installed, see Lab 1):

```bash
sudo apt install -y unzip zip
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh
sdk install gradle
gradle -version
```

### 2. Clone the source code

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorGradleApp.git
cd CalculatorGradleApp
```

### 3. Run the unit tests

```bash
gradle test
```

### 4. Build the application

```bash
gradle build
```

The artifact is generated at `build/libs/calculator.jar` (`archiveVersion` is empty in `build.gradle`, so the file has no version suffix).

### 5. Run the application

```bash
java -jar build/libs/calculator.jar
```

### 6. Verify the application

The app reads two numbers from standard input. Example run with `10` and `5`:

```
Enter first number: 10
Enter second number: 5
a = 10.0, b = 5.0
Sum: 15.0
Subtract: 5.0
Multiply: 50.0
Divide: 2.0
```

## Screenshots

Add your screenshots here (for example `gradle test`, `gradle build`, and the application output).
