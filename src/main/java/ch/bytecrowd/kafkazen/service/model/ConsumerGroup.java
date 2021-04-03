package ch.bytecrowd.kafkazen.service.model;

import org.apache.kafka.common.ConsumerGroupState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsumerGroup {

    private String groupId;
    private boolean isSimpleConsumerGroup;
    private Optional<ConsumerGroupState> state;
    private List<ConsumerGroupPartition> partition = new ArrayList<>();

    public String getGroupId() {
        return groupId;
    }

    public ConsumerGroup setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public boolean isSimpleConsumerGroup() {
        return isSimpleConsumerGroup;
    }

    public ConsumerGroup setSimpleConsumerGroup(boolean simpleConsumerGroup) {
        isSimpleConsumerGroup = simpleConsumerGroup;
        return this;
    }

    public Optional<ConsumerGroupState> getState() {
        return state;
    }

    public ConsumerGroup setState(Optional<ConsumerGroupState> state) {
        this.state = state;
        return this;
    }

    public List<ConsumerGroupPartition> getPartition() {
        return partition;
    }

    public ConsumerGroup setPartition(List<ConsumerGroupPartition> partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public String toString() {
        return "ConsumerGroup{" +
                "groupId='" + groupId + '\'' +
                ", isSimpleConsumerGroup=" + isSimpleConsumerGroup +
                ", state=" + state +
                ", partition=" + partition +
                '}';
    }
}
