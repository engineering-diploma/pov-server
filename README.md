# POV Server

is a simple Spring based server for managing POV project. For ease of deployment `docker-compose.yaml` was created

## Used technologies

- Spring
    - Boot
    - Repository
    - Data JPA
- MySQL
- Hibernate ORM
- RabbitMQ
- MinIO (S3 storage)

## How to run

### Docker

In this mode standalone server will be started. To function properly, one must provide all dependencies. Thoes are:
- MySQL - MinIO (S3)
- RabbitMQ

Once Rabbit was deployed, it should be customized. There is need to create topics, queues and bindings. Moreover POV
will not start if connection to Rabbit is not encrypted, thus Rabbit should be provided with valid certificate.

To provide maximal protection connection to DB should be also encrypted, otherwise server will fail

```bash
  docker run 
    -p <port>:8080 
    -e RABBIT_HOST=... 
    -e RABBIT_PORT=... 
    -e RABBIT_USERNAME=... 
    -e RABBIT_PASSWORD=... 
    -e API_USERNAME=...
    -e API_PASSWORD=...
    -e CONSOLE_USERNAME=...
    -e CONSOLE_PASSWORD=...
    -e S3_USER=...
    -e S3_PASSWORD=...
    -e S3_HOST=...
    -e DB_USERNAME=...
    -e DB_PASSWORD=...
    -e DB_HOST=...
    -e DB_NAME=...
    -e DB_PORT=...
    ghcr.io/engineering-diploma/pov-server:latest
```

### Docker Compose

Easiest way to start this server is to use `docker-compose.yaml`. Now one click deployment is available.

```bash
  docker-compose up
```

## What is POV

optical illusion that occurs when visual perception of an object does not cease for some time after the rays of light
proceeding from it have ceased to enter the eye

## Project goals

Main goal of this project is to create server allowing to mediate communication between mobile devices (remote
controllers) and single POV display. Server should workas as broker decopuling controller logic and worker software.
This enable us to separate concers of controll and operation, which in result bring more flexability on project
management.