package ch.bytecrowd.kafkazen.gui.controllers.menu;

import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.gui.utils.StageManager;
import ch.bytecrowd.kafkazen.gui.views.FxmlView;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class MenuController {

    @FXML
    private VBox root;

    private final StageManager stageManager = Main.stageManager;

    @FXML
    public void initialize() {

        final JFXButton home = createMenuButton("Kafka server connection", FxmlView.HOME);

        final JFXButton kafkaAdministration = createMenuButton("Kafka Administration", FxmlView.KAFKA_ADMINISTRATION);
        kafkaAdministration.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
        );

        final JFXButton publishToKafka = createMenuButton("Kafka Publisher", FxmlView.KAFKA_PUBLISHER);
        publishToKafka.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
        );

        final JFXButton consumeFromKafka = createMenuButton("Kafka Consumer", FxmlView.KAFKA_CONSUMER);
        consumeFromKafka.disableProperty().bind(
                Constants.VALID_KAFKA_SERVER.isEmpty()
        );

        root.getChildren().add(home);
        root.getChildren().add(kafkaAdministration);
        root.getChildren().add(publishToKafka);
        root.getChildren().add(consumeFromKafka);
    }

    private JFXButton createMenuButton(String text, FxmlView view) {
        final JFXButton menuBtn = new JFXButton(text);
        menuBtn.setTextAlignment(TextAlignment.CENTER);
        menuBtn.setPrefWidth(200);
        menuBtn.getStyleClass().add("transparent-button");
        menuBtn.setOnAction(e -> {
            try {
                stageManager.switchScene(view);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return menuBtn;
    }
}
