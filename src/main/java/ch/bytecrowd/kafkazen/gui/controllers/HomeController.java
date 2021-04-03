package ch.bytecrowd.kafkazen.gui.controllers;


import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.service.KafkaAdminService;
import ch.bytecrowd.kafkazen.service.KafkaAdminServiceFactory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.util.Optional;

public class HomeController extends JfxController {

    @FXML
    private JFXButton confirmBtn;
    @FXML
    private JFXTextField kafkaServerInp;

    public HomeController() {
        super(Main.stageManager);
    }

    @FXML
    public void initialize() {
        super.initialize();
        final String kafkaHost = Optional.ofNullable(Constants.VALID_KAFKA_SERVER.getValue())
                .orElse(Constants.PRESELECTED_KAFKA_SERVER.getValue());
        kafkaServerInp.setText(kafkaHost);
        confirmBtn.disableProperty().bind(kafkaServerInp.textProperty().isEmpty());
        initConfirmBtn();
    }

    private void initConfirmBtn() {
        confirmBtn.setOnAction(event -> {
            try {
                final String kafkaServerInpText = kafkaServerInp.getText();
                KafkaAdminService.healthCheck(kafkaServerInpText).ifPresentOrElse(e -> {
                    Constants.VALID_KAFKA_SERVER.setValue(null);
                    e.printStackTrace();
                    dialogUtil.showDialogError("Can not connect to Kafka", e.getCause() == null ? e :e.getCause());

                }, () -> {
                    Constants.VALID_KAFKA_SERVER.setValue(kafkaServerInpText);
                    dialogUtil.showDialogInfo("Connected to Kafka");
                    openMenu();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
