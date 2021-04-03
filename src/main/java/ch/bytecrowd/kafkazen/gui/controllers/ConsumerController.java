package ch.bytecrowd.kafkazen.gui.controllers;


import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.service.KafkaAdminService;
import ch.bytecrowd.kafkazen.service.KafkaAdminServiceFactory;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.kafka.clients.admin.TopicListing;

import javax.swing.*;
import java.io.File;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ConsumerController extends JfxController {

    @FXML
    private JFXButton confirmBtn;
    @FXML
    private JFXButton stopBtn;
    @FXML
    private JFXButton fileChooseBtn;
    @FXML
    private JFXCheckBox fetchAllInp;
    @FXML
    private JFXComboBox topicInp;

    @FXML
    private JFXToggleButton randomGroupIdToggle;

    @FXML
    private JFXTextField manualGroupIdInp;

    private File selectedFile;
    // private final BooleanProperty isStopped = new SimpleBooleanProperty(true);
    private final BooleanProperty isSelectedFileValid = new SimpleBooleanProperty(false);

    public ConsumerController() {
        super(Main.stageManager);
    }

    @FXML
    public void initialize() {
        super.initialize();

        confirmBtn.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
                        .or(topicInp.valueProperty().isNull())
                        .or(isSelectedFileValid.not())
                        .or(Constants.IS_CONSUMER_RUNNING)
        );
        stopBtn.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
                        .or(Constants.IS_CONSUMER_RUNNING.not())
        );

        initRandomGroupIdToggle();
        initTopics();
        initFileChooserBtn();
        initConfirmBtn();
        initStopBtn();
    }

    private void initRandomGroupIdToggle() {
        randomGroupIdToggle.setOnAction(event -> {
            if (randomGroupIdToggle.isSelected()) {
                manualGroupIdInp.setPromptText("Random generated group id will be deleted after consuming");
                manualGroupIdInp.setDisable(true);
            } else {
                manualGroupIdInp.setPromptText("Manual defined group ids won't be deleted after consuming");
                manualGroupIdInp.setDisable(false);
            }
        });
        randomGroupIdToggle.setSelected(false);
        randomGroupIdToggle.fire();
    }

    private void initFileChooserBtn() {
        fileChooseBtn.setOnAction(event -> {
            final JFileChooser fc = new JFileChooser(System.getProperty("java.io.tmpdir"));
            int returnVal = fc.showOpenDialog(new JFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                if (selectedFile.isDirectory()) {
                    isSelectedFileValid.setValue(true);
                    selectedFile = null;
                    dialogUtil.showDialogWarn("Invalid file");
                } else {
                    isSelectedFileValid.setValue(true);
                    fileChooseBtn.setText("File: " + selectedFile.getAbsolutePath());
                }
            } else {
                fileChooseBtn.setText("File: ");
            }
        });
    }

    private void initConfirmBtn() {
        confirmBtn.setOnAction(event -> {
            Constants.IS_CONSUMER_RUNNING.setValue(true);
            final String host = Constants.VALID_KAFKA_SERVER.getValue();
            dialogUtil.showDialogInfo("Consumer started");
            new Thread(() -> {
                try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(host);) {
                    final String topic = topicInp.getValue().toString();
                    Optional<String> groupId = Optional.empty();
                    if (!randomGroupIdToggle.isSelected()) {
                        groupId = Optional.ofNullable(manualGroupIdInp.getText());
                    }
                    if (fetchAllInp.isSelected()) {
                        service.fetchAllMessagesFromBeginning(topic, Constants.IS_CONSUMER_RUNNING, selectedFile, groupId);
                    } else {
                        service.fetchAllMessagesFromNow(topic, Constants.IS_CONSUMER_RUNNING, selectedFile, groupId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        dialogUtil.showDialogError("Can not consume from Kafka", e.getCause() == null ? e : e.getCause());
                    });
                } finally {
                    Platform.runLater(() -> {
                        dialogUtil.showDialogInfo("Consumer stopped");
                    });
                    Constants.IS_CONSUMER_RUNNING.setValue(false);
                }
            }).start();
        });
    }

    private void initStopBtn() {
        stopBtn.setOnAction(event -> Constants.IS_CONSUMER_RUNNING.setValue(false));
    }

    private void initTopics() {
        try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
            final TreeSet<String> topics = service.fetchAllTopics().stream()
                    .map(TopicListing::name)
                    .collect(Collectors.toCollection(TreeSet::new));
            topicInp.getItems().addAll(topics);
        } catch (Exception e) {
            dialogUtil.showDialogError("Can not communicate with Kafka", e.getCause() == null ? e : e.getCause());
        }
    }
}
