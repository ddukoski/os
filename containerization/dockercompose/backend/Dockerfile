# load entrypoint image
FROM eclipse-temurin

# set current directory, i.e., -> .
WORKDIR /usr/src/app

COPY ./src/SimpleHttpServer.java .
RUN javac SimpleHttpServer.java
CMD ["java", "SimpleHttpServer.java"]