FROM azul/zulu-openjdk:latest
MAINTAINER Max De Marzi<maxdemarzi@gmail.com>
EXPOSE 8080
COPY $HOME/target/uranusdb.jar uranusdb.jar
COPY $HOME/conf/uranus.conf /conf/application.conf
CMD ["java", "-jar", "uranusdb.jar"]
