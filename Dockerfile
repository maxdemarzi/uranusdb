FROM azul/zulu-openjdk:latest
MAINTAINER Max De Marzi<maxdemarzi@gmail.com>
EXPOSE 8080
COPY $ROOT/target/uranus-1.0-SNAPSHOT.jar uranus-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "uranus-1.0-SNAPSHOT.jar"]
