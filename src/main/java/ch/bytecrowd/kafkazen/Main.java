package ch.bytecrowd.kafkazen;

import ch.bytecrowd.kafkazen.gui.utils.Constants;
import ch.bytecrowd.kafkazen.gui.utils.StageManager;
import ch.bytecrowd.kafkazen.gui.views.FxmlView;
import io.quarkus.runtime.Quarkus;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage primaryStage;
    public static StageManager stageManager;

    public static void main(String[] args) {
        // Quarkus.run(args);
        Constants.PRESELECTED_KAFKA_SERVER.setValue("localhost:9092");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        // need to be done that ScrollPane displays correctly
        // is also used in StageManager.switchScene
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        stageManager = new StageManager();
        stageManager.switchScene(FxmlView.HOME);
    }
}
