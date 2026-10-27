# Lab 1: Building and Packaging a Java Application with Maven

Source code: https://github.com/Ibrahim-Adel15/CalculatorMavenApp

## Goal

Install Java and Maven, run the unit tests, build the application into `target/calculator.jar`, run it and verify it works.

## Steps

### 1. Install Java 17 and Maven

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven git
java -version
mvn -version
```

### 2. Clone the source code

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp
```

### 3. Run the unit tests

```bash
mvn test
```

The build should finish with `BUILD SUCCESS`.

### 4. Build the application

```bash
mvn package
```

The artifact is generated at `target/calculator.jar` (the name comes from `<finalName>calculator</finalName>` in `pom.xml`).

### 5. Run the application

```bash
java -jar target/calculator.jar
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

Dividing by `0` throws `ArithmeticException: Cannot divide by zero`.

## Screenshots

Add your screenshots here (for example `mvn test`, `mvn package`, and the application output).
