FROM maven:3.5.4-jdk-8
WORKDIR /usr/dk
COPY ./app.jar /usr/dk/
EXPOSE 8080
CMD ["java","-jar","app.jar"]