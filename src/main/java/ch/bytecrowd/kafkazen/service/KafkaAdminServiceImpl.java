package ch.bytecrowd.kafkazen.service;

import ch.bytecrowd.kafkazen.service.model.Pair;
import javafx.beans.property.BooleanProperty;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

class KafkaAdminServiceImpl implements KafkaAdminService, AutoCloseable {

    private final String host;
    private final AdminClient adminClient;

    KafkaAdminServiceImpl(String host) {
        this.host = host;
        adminClient = KafkaAdminClient.create(getAdminConfig(host));
    }

    private static Properties getAdminConfig(String host) {
        Properties adminConfig = new Properties();
        adminConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        adminConfig.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 1_000);
        return adminConfig;
    }

    private static Properties getConsumerConfig(String host, Optional<String> groupId) {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId.orElseGet(() -> UUID.randomUUID().toString() + "-kafkazen"));
        return consumerConfig;
    }

    private Properties getPublisherConfig() {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return producerConfig;
    }

    @Override
    public void createTopic(String topic) throws ExecutionException, InterruptedException {
        final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
        adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
    }

    @Override
    public Collection<TopicListing> fetchAllTopics() throws ExecutionException, InterruptedException {
        return adminClient.listTopics().listings().get();
    }

    @Override
    public Collection<TopicPartition> fetchTopicPartitions(String topic) throws ExecutionException, InterruptedException {
        return adminClient.listPartitionReassignments().reassignments().get().entrySet().stream()
                .map(Map.Entry::getKey)
                .filter(topicPartition -> topicPartition.topic().equals(topic))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public void deleteTopic(String topic) throws ExecutionException, InterruptedException {
        adminClient.deleteTopics(Collections.singletonList(topic)).all().get();
    }

    @Override
    public void deleteConsumerGroups(String groupIds) throws ExecutionException, InterruptedException {
        adminClient.deleteConsumerGroups(Collections.singletonList(groupIds)).all().get();
    }

    @Override
    public Collection<ConsumerGroupListing> fetchAllConsumerGroups() throws ExecutionException, InterruptedException {
        return adminClient.listConsumerGroups().all().get();
    }

    @Override
    public Collection<Pair<ConsumerGroupListing, ListConsumerGroupOffsetsResult>> fetchAllConsumerGroupOffsets() throws ExecutionException, InterruptedException {
        return adminClient.listConsumerGroups()
                .all()
                .get().stream()
                .map(s -> Pair.of(s, adminClient.listConsumerGroupOffsets(s.groupId())))
                .collect(Collectors.toList());
    }

    @Override
    public void fetchAllMessagesFromBeginning(String topic, BooleanProperty isRunning, File outputFile, Optional<String> customGroupId) throws ExecutionException, InterruptedException, IOException {
        Properties consumerConfig = getConsumerConfig(host, customGroupId);
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumeToFile(topic, isRunning, outputFile, consumerConfig, customGroupId.isEmpty());
    }

    @Override
    public void fetchAllMessagesFromNow(String topic, BooleanProperty isRunning, File outputFile, Optional<String> customGroupId) throws ExecutionException, InterruptedException, IOException {
        Properties consumerConfig = getConsumerConfig(host, customGroupId);
        consumeToFile(topic, isRunning, outputFile, consumerConfig, customGroupId.isEmpty());
    }

    private void consumeToFile(String topic, BooleanProperty isRunning, File outputFile, Properties consumerConfig, boolean withCleanUp) throws IOException, InterruptedException, ExecutionException {
        final List<String> groupIds = List.of(consumerConfig.get(ConsumerConfig.GROUP_ID_CONFIG).toString());
        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
             final FileOutputStream outputStream = new FileOutputStream(outputFile);) {
            outputStream.write("offset;topic;partition;headers;value;timestamp;timestampType\n".getBytes());
            consumer.subscribe(Collections.singletonList(topic));
            int totalEventsConsumed = 0;
            while (isRunning.getValue()) {
                final ConsumerRecords<String, String> polled = consumer.poll(Duration.ofMillis(1_000));
                final Iterator<ConsumerRecord<String, String>> iterator = polled.iterator();
                final StringBuilder csvLines = new StringBuilder();
                final String csvSeparator = ";";
                final String newLine = "\n";

                while (iterator.hasNext()) {
                    ConsumerRecord<String, String> record = iterator.next();
                    csvLines
                            .append(record.offset()).append(csvSeparator)
                            .append(record.topic()).append(csvSeparator)
                            .append(record.partition()).append(csvSeparator)
                            .append(record.headers()).append(csvSeparator)
                            .append(record.value().replaceAll("(\r)?\n", "")).append(csvSeparator)
                            .append(record.timestamp()).append(csvSeparator)
                            .append(record.timestampType())
                            .append(newLine);
                }
                System.out.println("[+] Total events consumed: " + (totalEventsConsumed += polled.count()));
                outputStream.write(csvLines.toString().getBytes());
                outputStream.flush();
            }
        } finally {
            if (withCleanUp) {
                adminClient.deleteConsumerGroups(groupIds).all().get();
            }
        }
    }

    @Override
    public RecordMetadata publish(String topic, String message) throws ExecutionException, InterruptedException {
        Properties producerConfig = getPublisherConfig();
        try (final KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);) {
            return producer.send(new ProducerRecord<>(topic, message)).get();
        }
    }

    @Override
    public void close() {
        adminClient.close();
    }
}
