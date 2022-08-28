package ch.bytecrowd.kafka;

import ch.bytecrowd.kafkazen.service.KafkaAdminService;
import ch.bytecrowd.kafkazen.service.KafkaAdminServiceFactory;
import ch.bytecrowd.kafkazen.service.model.ConsumerGroup;
import ch.bytecrowd.kafkazen.service.model.ConsumerGroupPartition;
import ch.bytecrowd.kafkazen.service.model.TopicFat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@QuarkusTest
@QuarkusTestResource(KafkaTestContainerRessource.class)
public class KafkaClientTest {

    private String KAFKA_HOST = "localhost:9092";
    private KafkaAdminService service;

    private static ConsumerGroupPartition toConsumerGroupPartition(Map.Entry<TopicPartition, OffsetAndMetadata> entry) {
        final TopicPartition key = entry.getKey();
        final OffsetAndMetadata value = entry.getValue();

        return new ConsumerGroupPartition()
                .setTopic(key.topic())
                .setPartition(key.partition())
                .setLeaderEpoch(value.leaderEpoch())
                .setMetadata(value.metadata())
                .setOffset(value.offset());
    }

    @BeforeEach
    public void beforeEach() {
        KAFKA_HOST = KafkaTestContainerRessource.kafkaContainer.getBootstrapServers();
        service = KafkaAdminServiceFactory.createService(KAFKA_HOST);
    }

    @Test
    public void test_fetchAllTopics() throws ExecutionException, InterruptedException {
        service.fetchAllTopics().forEach(System.out::println);
    }

    @Test
    public void test_fetchAllConsumerGroups() throws ExecutionException, InterruptedException {
        service.fetchAllConsumerGroups().forEach(System.out::println);
    }

    @Test
    public void test_fetchAllConsumerGroupOffsets() throws ExecutionException, InterruptedException {
        final List<ConsumerGroup> consumerGroups = service.fetchAllConsumerGroupOffsets().stream().map(pair -> {
            try {
                final List<ConsumerGroupPartition> partitions = pair.getSecond().partitionsToOffsetAndMetadata().get().entrySet().stream()
                        .map(KafkaClientTest::toConsumerGroupPartition)
                        .collect(Collectors.toList());

                return new ConsumerGroup()
                        .setGroupId(pair.getFirst().groupId())
                        .setSimpleConsumerGroup(pair.getFirst().isSimpleConsumerGroup())
                        .setState(pair.getFirst().state())
                        .setPartition(partitions);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new ConsumerGroup();
            }
        }).collect(Collectors.toList());


        consumerGroups.stream().flatMap(groups -> groups.getPartition().stream())
                .map(ConsumerGroupPartition::getTopic)
                .distinct()
                .map(topic -> {
                    final List<ConsumerGroup> consumerGroupStream = consumerGroups.stream().filter(consumerGroup -> consumerGroup.getPartition().stream().anyMatch(consumerGroupPartition -> consumerGroupPartition.getTopic().equals(topic))).collect(Collectors.toList());
                    return new TopicFat().setTopic(topic).setTopicGroups(consumerGroupStream);
                })
                .forEach(System.out::println);
                // .collect(Collectors.toList());
    }

    @Test
    public void test_publish() throws ExecutionException, InterruptedException {
        final RecordMetadata record = service.publish("Genre", "{ \"eventType\": \"CREATE\" , \"entity\": { \"name\": \"TEST\"} }");
        System.out.println("Published: " + record.timestamp());
    }
}
