package ch.bytecrowd.kafkazen.gui.utils;

import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.views.FxmlView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class StageManager {

    public void switchScene(FxmlView fxmlView) throws IOException {
        final Parent root = loadView(fxmlView);



        Main.primaryStage.setMinWidth(420);
        Main.primaryStage.setMinHeight(Main.primaryStage.getHeight());
        root.prefHeight(Main.primaryStage.getHeight());
        root.prefWidth(Main.primaryStage.getWidth());

        final Scene scene = new Scene(root, Main.primaryStage.getHeight(), Main.primaryStage.getWidth());
        addCss(scene);

        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
    }

    public Parent loadView(FxmlView fxmlView) throws IOException {
        final URL resource = StageManager.class.getClassLoader().getResource(fxmlView.getResourcePath());
        final FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setControllerFactory(Main.springContext::getBean);
        fxmlLoader.setLocation(resource);
        final Parent root = fxmlLoader.load();

//        final Object controller = fxmlLoader.getController();
//        if (controller instanceof JfxController)
//            ((JfxController)controller).onShow();

        return root;
    }

    private boolean addCss(Scene scene) {
        return scene.getStylesheets().add(StageManager.class.getClassLoader().getResource("view/css/main-theme.css").toExternalForm());
    }
}
