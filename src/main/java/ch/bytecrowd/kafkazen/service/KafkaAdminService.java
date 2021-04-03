package ch.bytecrowd.kafkazen.service;

import ch.bytecrowd.kafkazen.service.model.Pair;
import javafx.beans.property.BooleanProperty;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public interface KafkaAdminService extends AutoCloseable {

    Collection<TopicListing> fetchAllTopics() throws ExecutionException, InterruptedException;

    Collection<TopicPartition> fetchTopicPartitions(String topic) throws ExecutionException, InterruptedException;

    void deleteTopic(String topic) throws ExecutionException, InterruptedException;

    void deleteConsumerGroups(String groupIds) throws ExecutionException, InterruptedException;

    Collection<ConsumerGroupListing> fetchAllConsumerGroups() throws ExecutionException, InterruptedException;

    Collection<Pair<ConsumerGroupListing, ListConsumerGroupOffsetsResult>> fetchAllConsumerGroupOffsets() throws ExecutionException, InterruptedException;

    void fetchAllMessagesFromBeginning(String topic, BooleanProperty isRunning, File outputFile, Optional<String> customGroupId) throws ExecutionException, InterruptedException, IOException;

    void fetchAllMessagesFromNow(String topic, BooleanProperty isRunning, File outputFile, Optional<String> customGroupId) throws ExecutionException, InterruptedException, IOException;

    RecordMetadata publish(String topic, String message) throws ExecutionException, InterruptedException;

    static Optional<Exception> healthCheck(String host) {
        Properties adminConfig = new Properties();
        adminConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        adminConfig.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 1_000);
        try (final AdminClient adminClient = KafkaAdminClient.create(adminConfig)) {
            StringBuilder nodes = new StringBuilder();
            Node node;
            for (Iterator it = ((Collection) adminClient.describeCluster().nodes().get()).iterator(); it.hasNext(); nodes.append(node.host()).append(':').append(node.port())) {
                node = (Node) it.next();
                if (nodes.length() > 0) {
                    nodes.append(',');
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    void createTopic(String topic) throws ExecutionException, InterruptedException;
}
