# POV Server

is a simple Spring based server for managing POV project

## Used technologies

- Spring
    - Boot
    - Repository
    - Data JPA
- MySQL
- Hibernate ORM
- Kafka
- RabbitMQ

## What is POV

optical illusion that occurs when visual perception of an object does not cease for some time after the rays of light
proceeding from it have ceased to enter the eye

## Project goals

Main goal of this project is to create server allowing to mediate communication between mobile devices (remote
controllers) and single POV display. Server should workas as broker decopuling controller logic and worker software.
This enable us to separate concers of controll and operation, which in result bring more flexability on project
management.