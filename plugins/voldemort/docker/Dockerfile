FROM lwieske/java-8:jdk-8u112-slim
RUN apk update && apk add ca-certificates wget && update-ca-certificates && apk add openssl && apk add --update bash
ENV VOLDEMORT_VERSION=release-1.10.23-cutoff
RUN wget https://github.com/voldemort/voldemort/archive/$VOLDEMORT_VERSION.zip
RUN unzip $VOLDEMORT_VERSION.zip && mv voldemort-* voldemort
WORKDIR /voldemort/
ENV VOLDEMORT_HOME /voldemort/config/single_node_cluster
RUN ./gradlew clean build -x test
EXPOSE 6666 6667 8081
CMD ./bin/voldemort-server.sh