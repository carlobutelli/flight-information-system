FROM maven:3.6.3-jdk-11-slim
MAINTAINER Carlo Butelli <dev.butelli@gmail.com>

ADD . /code
WORKDIR /code

EXPOSE 8081

RUN mvn dependency:tree
RUN mvn package

RUN chmod 755 /code/docker-entrypoint.sh
ENTRYPOINT ["/code/docker-entrypoint.sh"]
CMD ["run"]