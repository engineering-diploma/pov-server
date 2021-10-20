FROM openjdk:11
RUN apt update && apt upgrade -y
RUN apt install ffmpeg -y
RUN mkdir /pov-server/
RUN mkdir /pov-server/data/
WORKDIR /pov-server
COPY . /pov-server/
RUN ./mvnw package
EXPOSE 8080
VOLUME /pov-server/data/
ENTRYPOINT java -jar target/pov-server-1.0.jar