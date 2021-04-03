package ch.bytecrowd.kafkazen.gui.controllers;


import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.service.KafkaAdminService;
import ch.bytecrowd.kafkazen.service.KafkaAdminServiceFactory;
import ch.bytecrowd.kafkazen.service.model.ConsumerGroup;
import ch.bytecrowd.kafkazen.service.model.ConsumerGroupPartition;
import ch.bytecrowd.kafkazen.service.model.TopicFat;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AdministrationController extends JfxController {

    final List<TopicFat> topics = new ArrayList<>();

    @FXML
    private JFXComboBox<String> topicInp;
    @FXML
    private JFXButton topicDelBtn;
    @FXML
    private JFXComboBox<String> groupInp;
    @FXML
    private JFXButton groupDelBtn;
    @FXML
    private JFXTextField newTopicInp;
    @FXML
    private JFXButton topicAddBtn;
    @FXML
    private Label topicDetails;
    @FXML
    private ContextMenu topicInfoCtxMenu;

    private StringProperty selectedTopic = new SimpleStringProperty();
    private StringProperty selectedGroup = new SimpleStringProperty();

    public AdministrationController() {
        super(Main.stageManager);
    }

    @FXML
    public void initialize() {
        super.initialize();

        initTopics();

        topicDelBtn.disableProperty().bind(selectedTopic.isEmpty().or(selectedTopic.isNull()));
        groupDelBtn.disableProperty().bind(selectedGroup.isEmpty().or(selectedGroup.isNull()));
        topicAddBtn.disableProperty().bind(newTopicInp.textProperty().isEmpty().or(newTopicInp.textProperty().isNull()));
        topicDetails.setText("\n    → select a topic and its information will be displayed here");

        initContextMenu();
        initTopicAddBtn();
        initTopicDelBtn();
        initGroupDelBtn();
        initTopicSelect();
        initGroupSelect();
    }

    private void initContextMenu() {
        final MenuItem copy_content_to_clipboard = new MenuItem("Copy content to clipboard");
        copy_content_to_clipboard.setOnAction(event -> {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(
                            new StringSelection(topicDetails.getText()),
                            null
                    );
            dialogUtil.showDialogInfo("content is copied to your clipboard");
        });
        topicInfoCtxMenu.getItems().setAll(copy_content_to_clipboard);
    }

    private void initTopicAddBtn() {
        topicAddBtn.setOnAction(event -> {
            try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
                service.createTopic(newTopicInp.getText());
                dialogUtil.showDialogInfo(newTopicInp.getText() + " was created");
                initTopics();
            } catch (Exception e) {
                dialogUtil.showDialogInfo(newTopicInp.getText() + " could not be created");
                e.printStackTrace();
            }
        });
    }

    private void initTopicDelBtn() {
        topicDelBtn.setOnAction(event -> {
            dialogUtil.showDialogConfirm("Do you really want to delete this Topic ?\n" + selectedTopic.getValue(), () -> {
                try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
                    service.deleteTopic(selectedTopic.get());
                    dialogUtil.showDialogInfo(selectedTopic.getValue() + " was deleted");
                    selectedTopic.setValue(null);
                    selectedGroup.setValue(null);
                    topicInp.setValue(null);
                    groupInp.setValue(null);
                    initTopics();
                } catch (Exception e) {
                    dialogUtil.showDialogInfo(selectedTopic.getValue() + " could not be deleted");
                    e.printStackTrace();
                }
            }, () -> {
            });
        });
    }

    private void initGroupDelBtn() {
        groupDelBtn.setOnAction(event -> {
            dialogUtil.showDialogConfirm("Do you really want to delete this Consumer Group ?\n" + selectedGroup.getValue(), () -> {
                try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
                    service.deleteConsumerGroups(selectedGroup.get());
                    final List<String> updatedGroups = groupInp.getItems().stream()
                            .filter(s -> !s.equals(selectedGroup.getValue()))
                            .collect(Collectors.toList());
                    dialogUtil.showDialogInfo(selectedGroup.getValue() + " was deleted");
                    selectedGroup.setValue(null);
                    groupInp.setValue(null);
                    groupInp.getItems().setAll(updatedGroups);
                } catch (Exception e) {
                    dialogUtil.showDialogInfo(selectedGroup.getValue() + " could not be deleted");
                    e.printStackTrace();
                }
            }, () -> {
            });
        });
    }

    private void initTopicSelect() {
        topicInp.setOnAction(event -> {
            final String newTopic = topicInp.getValue();
            final StringBuilder builder = new StringBuilder();
            topics.stream()
                    .filter(topicFat -> topicFat.getTopic().equals(newTopic))
                    .findAny()
                    .ifPresent(topic -> {
                        topic.getTopicGroups().forEach(consumerGroup -> {
                            builder.append("-------------------------[CONSUMER]-----------------------------").append("\n")
                                    .append("    group-id: ").append(consumerGroup.getGroupId()).append("\n")
                                    .append("    state: ").append(consumerGroup.getState().orElse(null)).append("\n");

                            consumerGroup.getPartition().forEach(consumerGroupPartition -> {
                                builder.append("    ---------------------[PARTITION]----------------------------").append("\n")
                                        .append("        partition: ").append(consumerGroupPartition.getPartition()).append("\n")
                                        .append("        offset: ").append(consumerGroupPartition.getOffset()).append("\n")
                                        .append("        leader-epoch: ").append(consumerGroupPartition.getLeaderEpoch()).append("\n")
                                        .append("        metadata: ").append(consumerGroupPartition.getMetadata()).append("\n");
                            });
                        });
                    });
            topicDetails.setText(builder.length() > 1 ? builder.toString() : "no consumers found for topic:\n    - " + newTopic);;
            groupInp.getItems().setAll(List.of());
            if (newTopic != null && !newTopic.equals(selectedTopic.getValue())) {
                selectedTopic.set(newTopic);
                final TreeSet<String> relatedGroups = topics.stream()
                        .filter(topic -> topic.getTopic().equals(selectedTopic.getValue()))
                        .flatMap(topicFat -> topicFat.getTopicGroups().stream())
                        .map(ConsumerGroup::getGroupId)
                        .collect(Collectors.toCollection(TreeSet::new));
                groupInp.getItems().setAll(relatedGroups);
            }
        });
    }

    private void initGroupSelect() {
        groupInp.setOnAction(event -> {
            final String newGroup = groupInp.getValue();
            if (newGroup != null && !newGroup.equals(selectedTopic.getValue())) {
                selectedGroup.set(newGroup);
            }
        });
    }

    private final void initTopics() {
        topics.clear();
        List<TopicFat> fatTopics = null;
        try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
            fatTopics = fetchAllTopics(service);
            this.topics.addAll(fatTopics);

            topicInp.getItems().setAll(
                    fatTopics.stream().map(TopicFat::getTopic).collect(Collectors.toCollection(TreeSet::new))
            );
        } catch (Exception e) {
            dialogUtil.showDialogError("Can not communicate with Kafka", e.getCause() == null ? e : e.getCause());
        }
    }

    private List<TopicFat> fetchAllTopics(KafkaAdminService kafkaAdminService) throws ExecutionException, InterruptedException {
        final List<ConsumerGroup> consumerGroups = fetchConsumerGroups(kafkaAdminService);
        final List<TopicFat> topicFats = mapToNotEmptyTopics(consumerGroups);
        final Set<TopicFat> emptyTopics = fetchEmptyTopics(kafkaAdminService, topicFats);
        topicFats.addAll(emptyTopics);
        return topicFats;
    }

    private Set<TopicFat> fetchEmptyTopics(KafkaAdminService kafkaAdminService, List<TopicFat> topicFats) throws ExecutionException, InterruptedException {
        final Set<String> allFatTopicNames = topicFats.stream()
                .map(TopicFat::getTopic)
                .collect(Collectors.toSet());

        return kafkaAdminService.fetchAllTopics().stream()
                .map(TopicListing::name)
                .filter(s -> !allFatTopicNames.contains(s))
                .map(topic -> new TopicFat().setTopic(topic))
                .collect(Collectors.toSet());
    }

    private List<TopicFat> mapToNotEmptyTopics(List<ConsumerGroup> consumerGroups) {
        return consumerGroups.stream().flatMap(groups -> groups.getPartition().stream())
                .map(ConsumerGroupPartition::getTopic)
                .distinct()
                .map(topic -> {
                    final List<ConsumerGroup> consumerGroupStream = consumerGroups.stream()
                            .filter(consumerGroup -> consumerGroup.getPartition().stream()
                                    .anyMatch(consumerGroupPartition -> consumerGroupPartition.getTopic().equals(topic))
                            ).collect(Collectors.toList());
                    return new TopicFat().setTopic(topic).setTopicGroups(consumerGroupStream);
                }).collect(Collectors.toList());
    }

    private List<ConsumerGroup> fetchConsumerGroups(KafkaAdminService kafkaAdminService) throws ExecutionException, InterruptedException {
        return kafkaAdminService.fetchAllConsumerGroupOffsets().stream().map(pair -> {
            try {
                final List<ConsumerGroupPartition> partitions = pair.getSecond().partitionsToOffsetAndMetadata().get().entrySet().stream()
                        .map(AdministrationController::toConsumerGroupPartition)
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
    }

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
}
