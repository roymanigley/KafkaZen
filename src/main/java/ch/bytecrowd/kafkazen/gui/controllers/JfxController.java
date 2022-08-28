package ch.bytecrowd.kafkazen.gui.controllers;

import ch.bytecrowd.kafkazen.gui.utils.DialogUtil;
import ch.bytecrowd.kafkazen.gui.utils.StageManager;
import ch.bytecrowd.kafkazen.gui.views.FxmlView;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class JfxController {

    @FXML
    protected StackPane stackPane;
    @FXML
    private JFXHamburger menuBtn;
    @FXML
    private JFXDrawer menuDrawer;

    private final StageManager stageManager;
    protected DialogUtil dialogUtil;

    public JfxController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public void initialize() {

        dialogUtil = new DialogUtil(stackPane);

        loadMenu();
        initMenuButton();
    }

    private void loadMenu() {
        try {
            final Parent menuNode = stageManager.loadView(FxmlView.MENU);
            menuDrawer.maxWidth(Double.MAX_VALUE);
            menuDrawer.getChildren().add(menuNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMenuButton() {
        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(menuBtn);
        burgerTask.setRate(-1);
        menuDrawer.setVisible(false);

        menuDrawer.setOnMouseClicked(event -> {
            menuDrawer.close();
        });
        menuDrawer.setOnDrawerClosing(event -> {
            menuDrawer.setVisible(false);

            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();
        });
        menuDrawer.setOnDrawerOpened(event -> {
            menuDrawer.setVisible(true);
        });

        menuBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, t -> {
            if (menuDrawer.isClosed())
                menuDrawer.open();
            else
                menuDrawer.close();
            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();
        });
    }

    protected void navigateTo(FxmlView view) {
        try {
            stageManager.switchScene(view);
        } catch (IOException e) {
            e.printStackTrace();
            dialogUtil.showDialogError("Scene navigation failed", e);
        }
    }

    protected void navigateTo(FxmlView view, Runnable andThen) {
        try {
            stageManager.switchScene(view);
            Platform.runLater(andThen::run);
        } catch (IOException e) {
            e.printStackTrace();
            dialogUtil.showDialogError("Scene navigation failed", e);
        }
    }

    protected void openMenu() {
        final MouseEvent mouseEvent = new MouseEvent(
                MouseEvent.MOUSE_PRESSED, 0, 0,
                0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, false, true, false, false, false,
                false, null);
        menuBtn.fireEvent(mouseEvent);
    }

    public void onShow() {
        // will be overwritten from child classes, this method will be called from StageManager after sceneSwitch
    }
}
