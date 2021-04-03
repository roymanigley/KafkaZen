package ch.bytecrowd.kafkazen.service.model;

import java.util.Optional;

public class ConsumerGroupPartition {

    private String topic;
    private int partition;
    private long offset;
    private String metadata;
    private Integer leaderEpoch;

    public String getTopic() {
        return topic;
    }

    public ConsumerGroupPartition setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getPartition() {
        return partition;
    }

    public ConsumerGroupPartition setPartition(int partition) {
        this.partition = partition;
        return this;
    }

    public long getOffset() {
        return offset;
    }

    public ConsumerGroupPartition setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public ConsumerGroupPartition setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public Integer getLeaderEpoch() {
        return leaderEpoch;
    }

    public ConsumerGroupPartition setLeaderEpoch(Optional<Integer> leaderEpoch) {
        this.leaderEpoch = leaderEpoch.orElse(null);
        return this;
    }

    @Override
    public String toString() {
        return "ConsumerGroupPartition{" +
                "topic=" + topic +
                "partition=" + partition +
                ", offset=" + offset +
                ", metadata='" + metadata + '\'' +
                ", leaderEpoch=" + leaderEpoch +
                '}';
    }
}
