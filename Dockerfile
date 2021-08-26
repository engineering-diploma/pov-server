FROM openjdk:11
RUN apt update && apt upgrade -y
RUN apt install ffmpeg -y
RUN mkdir /pov-server/
RUN mkdir /pov-server/target/data/ -p
RUN mkdir /pov-server/youtube-movies/
WORKDIR /pov-server
COPY . /pov-server/
RUN ./mvnw package
EXPOSE 8080
VOLUME /pov-server/target/data/
VOLUME /pov-server/youtube-movies/
ENTRYPOINT java -jar target/pov-server-1.0.jar