package ch.bytecrowd.kafkazen.gui.controllers;


import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.service.KafkaAdminService;
import ch.bytecrowd.kafkazen.service.KafkaAdminServiceFactory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import org.apache.kafka.clients.admin.TopicListing;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PublisherController extends JfxController {

    @FXML
    private JFXButton confirmBtn;
    @FXML
    private JFXTextArea messageInp;
    @FXML
    private JFXComboBox topicInp;

    public PublisherController() {
        super(Main.stageManager);
    }

    @FXML
    public void initialize() {
        super.initialize();

        confirmBtn.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
                .or(messageInp.textProperty().isEmpty())
                .or(topicInp.valueProperty().isNull())
        );

        initTopics();
        initConfirmBtn();
        initMessageInp();
    }

    private void initTopics() {
        try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(Constants.VALID_KAFKA_SERVER.getValue());) {
            final TreeSet<String> topics = service.fetchAllTopics().stream()
                    .map(TopicListing::name)
                    .collect(Collectors.toCollection(TreeSet::new));
            topicInp.getItems().addAll(topics);
        } catch (Exception e) {
            dialogUtil.showDialogError("Can not communicate with Kafka", e.getCause() == null ? e :e.getCause());
        }
    }

    private void initConfirmBtn() {
        confirmBtn.setOnAction(event -> {
            final String host = Constants.VALID_KAFKA_SERVER.getValue();
            try (final KafkaAdminService service = KafkaAdminServiceFactory.createService(host);) {
                service.publish(topicInp.getValue().toString(), messageInp.getText());
                dialogUtil.showDialogInfo("Message published to Kafka");
            } catch (Exception e) {
                dialogUtil.showDialogError("Can not communicate with Kafka", e.getCause() == null ? e :e.getCause());
            }
        });
    }

    private void initMessageInp() {
        messageInp.setText("{\n" +
                "     \"event\": \"CREATE\",\n" +
                "     \"entity\": {\n" +
                "        \"name\": \"Test " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) +"\"\n" +
                "    }\n" +
                "}");
    }
}
