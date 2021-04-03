package ch.bytecrowd.kafkazen.service.model;

import java.util.ArrayList;
import java.util.List;

public class TopicFat {

    private String topic;

    private List<ConsumerGroup> topicGroups = new ArrayList<>();

    public String getTopic() {
        return topic;
    }

    public TopicFat setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public List<ConsumerGroup> getTopicGroups() {
        return topicGroups;
    }

    public TopicFat setTopicGroups(List<ConsumerGroup> topicGroups) {
        this.topicGroups = topicGroups;
        return this;
    }

    @Override
    public String toString() {
        return "TopicFat{" +
                "topic='" + topic + '\'' +
                ", topicGroups=" + topicGroups +
                '}';
    }
}
