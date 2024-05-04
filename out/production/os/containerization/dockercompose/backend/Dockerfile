FROM eclipse-temurin
WORKDIR /usr/src/app
COPY ./src/SimpleHttpServer.java .
RUN javac SimpleHttpServer.java
CMD ["java", "SimpleHttpServer"]