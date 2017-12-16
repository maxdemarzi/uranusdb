FROM azul/zulu-openjdk:latest
MAINTAINER Max De Marzi<maxdemarzi@gmail.com>
EXPOSE 8080
COPY ./conf/uranus.conf /conf/application.conf
COPY ./target/uranusdb.jar uranusdb.jar
CMD ["java", "-jar", "uranusdb.jar"]
