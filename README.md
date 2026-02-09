# Video Share Platform

Enterprise video sharing platform built using Spring Boot and a 3-layer architecture.

## Tech Stack
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Validation
- Lombock

## How to Run the Application
### Prerequisites
- Java 17+
- Maven 3.9+ (or Maven Wrapper)

## Run Locally
```bash
mvn spring-boot:run
```

---

# Team Workflow Guide

This section explains how to work with the repository as a team.
Please read this before making any commits.

## Repository Overview

- **Master branch (`master`)**
    - Stable, demo-ready code only
    - Used for submissions and presentations
    - Do NOT commit directly to this branch

- **Development branch (`dev`)**
    - Integration branch
    - All work is merged here
    - Default working branch

- **Feature/Bugfix branches (`<type>/<short-description>`)**
    - Individual work branches
    - Created from `dev`
    - Merged back into `dev` via Pull Request

## First-Time Setup

### 1. Clone the repository
```bash
git clone https://github.com/starsvoyage/video-share.git
cd video-share
```

### 2. Switch to dev branch
```bash
git checkout dev
```

### 3. Verify the project builds
Before making any changes, make sure the project runs:
```bash
mvn clean
mvn test
```
Or using the Maven Wrapper:
```bash
./mvnw clean
./mvnw test
```

### 4. Run the application and test the health endpoint
Start the application:
```bash
mvn spring-boot:run
```
Then verify the service is running:
```Bash
http://localhost:8080/api/health
```
Expected response:
```Bash
OK
```

## Creating a Branch
All work must branch out of dev:
```bash
git checkout dev
git pull origin dev
git checkout -b <type>/<short-description>
```
## Before You Commit
Before committing every time, do the following:
### 1. Make sure you are NOT on `master`
```bash
git branch
```
### 2. Clean adn build the project
```bash
mvn clean
```
If it does not build cleanly, do NOT commit.

### 3. Check what you are committing
```bash
git status
```
Only commit files you intentionally changed.

## Pushing Your Work
Push your branch:
```bash
git push origin <type>/<branch-name>
```
Then open a Pull Request:

- Base branch: dev
- Do not merge our own PR
- PR Reviews shall be done by other team members







