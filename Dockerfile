FROM virtuslab/scala-cli:latest AS build
RUN mkdir /app
COPY ["App.scala", "build.sh", "run.sh", "/app/"]
WORKDIR /app
RUN source ./build.sh && fatJar

FROM openjdk:18-oraclelinux8
RUN mkdir /app
COPY --from=build /app/* /app
WORKDIR /app
EXPOSE 8080
CMD /app/run.sh