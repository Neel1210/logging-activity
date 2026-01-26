# Logging Activity – Log4j Sensitive Data Masking

## Overview

**Logging Activity** is a Spring Boot application created to experiment with **masking sensitive data in logs** using **Log4j**

This project demonstrates how sensitive information can be masked at the **logging framework level** (not in business
logic) using a reusable utility dependency.

---

## Application Details

- **Application Name:** logging-activity
- **Application Framework:** Spring Boot
- **Base Package:** `in.org.nnm`
- **Logging Framework:** Log4j
- **Primary Goal:** Mask sensitive data in application logs

---

## Project Structure

```
│in.org.nnm
├── src/main/java
│   ├── controller
│   │   └── LoggingController.java
│   │       └── Endpoint: GET /logging/display
│   │
│   └── utils
│       └── csvparser
│           └── display()
│               └── Reads logs from a TXT file
│
├── src/main/resources
│   ├── log4j2-configurations
│   │   ├── log4j2.xml
│   │   ├── log4j2.yml
│   │   ├── log4j2.properties
│   │   └── log4j2.json
│   │
│   ├── logs.csv
│   └── application.properties
│
└── pom.xml
```

---

## External Dependency Used

This project uses a custom utility for Log4j masking:

```xml

<dependency>
    <groupId>in.org.nnm</groupId>
    <artifactId>log4j-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Purpose of log4j-utils

- Centralized masking logic for Log4j
- Prevents sensitive data leakage in logs
- Reusable across multiple Spring Boot applications
- Keeps business code clean and secure

### Sensitive Data Masking – Concept

1. Application generates logs normally
2. Log messages pass through Log4j layouts / rewrite policies
3. Masking logic from log4j-utils is applied
4. Sensitive data is replaced with masked values (e.g. ****)
5. Only masked logs are printed or stored

---

## Log4j Configuration Formats

This project includes **four different Log4j configuration formats** to experiment with and compare behavior while
masking sensitive data.

| File Name           |   Format   |
|:--------------------|:----------:|
| `log4j2.xml`        |    XML     |
| `log4j2.yml`        |    YAML    |
| `log4j2.json`       |    JSON    |
| `log4j2.properties` | Properties |

Each configuration file demonstrates:

- Console and JSON appenders
- Layout configurations
- Sensitive data masking support
- Different ways of defining Log4j settings

---

### Switching Log4j Configuration

You can switch between configurations by updating the following property in `application.properties`:

```properties
logging.config=classpath:log4j2-configurations/log4j2.xml
```

(Change the file name to test other formats)

---

## How to Run the Application

### Prerequisites

- Java 17 or higher
- Maven
- Spring Boot

---

### Steps

```bash
git clone https://github.com/Neel1210/logging-activity.git
cd logging-activity
mvn clean install
mvn spring-boot:run
```

---

### API Endpoint

Once the application starts, access the endpoint at:

`http://localhost:8080/logging/display`

---

## Author

* **Neelesh Mehar**
* **Software Engineer**
* **Focus areas:** Java, Spring Boot, Logging, Security, Clean Architecture

---