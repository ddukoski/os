FROM eclipse-temurin
WORKDIR usr/src/app
COPY . .
RUN mkdir datavirt
RUN javac Incrementing.java
CMD ["java", "Incrementing.java"]