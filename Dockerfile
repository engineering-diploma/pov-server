FROM openjdk:11
RUN apt update && apt upgrade -y
RUN apt install ffmpeg -y
RUN mkdir /pov-server/
WORKDIR /pov-server
COPY . /pov-server/
RUN ./mvnw package
EXPOSE 8080
ENTRYPOINT java -jar target/pov-server-1.0.jar