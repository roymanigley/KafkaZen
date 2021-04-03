    ██ ▄█▀▄▄▄        █████▒██ ▄█▀▄▄▄      ▒███████▒▓█████  ███▄    █ 
    ██▄█▒▒████▄    ▓██   ▒ ██▄█▒▒████▄    ▒ ▒ ▒ ▄▀░▓█   ▀  ██ ▀█   █ 
    ▓███▄░▒██  ▀█▄  ▒████ ░▓███▄░▒██  ▀█▄  ░ ▒ ▄▀▒░ ▒███   ▓██  ▀█ ██▒
    ▓██ █▄░██▄▄▄▄██ ░▓█▒  ░▓██ █▄░██▄▄▄▄██   ▄▀▒   ░▒▓█  ▄ ▓██▒  ▐▌██▒
    ▒██▒ █▄▓█   ▓██▒░▒█░   ▒██▒ █▄▓█   ▓██▒▒███████▒░▒████▒▒██░   ▓██░
    ▒ ▒▒ ▓▒▒▒   ▓▒█░ ▒ ░   ▒ ▒▒ ▓▒▒▒   ▓▒█░░▒▒ ▓░▒░▒░░ ▒░ ░░ ▒░   ▒ ▒ 
    ░ ░▒ ▒░ ▒   ▒▒ ░ ░     ░ ░▒ ▒░ ▒   ▒▒ ░░░▒ ▒ ░ ▒ ░ ░  ░░ ░░   ░ ▒░
    ░ ░░ ░  ░   ▒    ░ ░   ░ ░░ ░  ░   ▒   ░ ░ ░ ░ ░   ░      ░   ░ ░ 
    ░  ░        ░  ░       ░  ░        ░  ░  ░ ░       ░  ░         ░ 
                                          ░                          


> A platform independent Kafka Client UI, written in JavaFX
> - Connect to Kafka
> - Administration
>     - delete Topics
>     - delete Consumers
>     - create Topics
> - Publish
> - Subscribe (from current or from begin) 

![kafkazen](docs/images/kafkazen.png)

## Build

    ./mvnw clean package
    
    
## Run

    java -jar kafkazen-1.0.0-SNAPSHOT-runner.jar
    
## Run dev

    ./mvnw clean javafx:run 
    
> To prevent GTK Warnings/Errors on GTK 2 add this property when running the application
        
        -Djdk.gtk.version=2
## Example Kafka `docker-compose`
### start    
    docker-compose up -d
### stop    
    docker-compose down
### logs    
    docker-compose logs
### `docker-compose.yml`
```yaml
version: '2'
services:
  zookeeper:
    image: strimzi/kafka:0.20.1-kafka-2.6.0
    command: [
        "sh", "-c",
        "bin/zookeeper-server-start.sh config/zookeeper.properties"
    ]
    ports:
      - "2181:2181"
    environment:
      LOG_DIR: /tmp/logs

  kafka:
    image: strimzi/kafka:0.20.1-kafka-2.6.0
    command: [
        "sh", "-c",
        "bin/kafka-server-start.sh config/server.properties --override listeners=$${KAFKA_LISTENERS} --override advertised.listeners=$${KAFKA_ADVERTISED_LISTENERS} --override zookeeper.connect=$${KAFKA_ZOOKEEPER_CONNECT}"
    ]
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      LOG_DIR: "/tmp/logs"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```
