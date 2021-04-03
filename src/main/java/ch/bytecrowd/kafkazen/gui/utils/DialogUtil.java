package ch.bytecrowd.kafkazen.gui.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DialogUtil {

    private final StackPane pane;
    private static JFXButton close;
    private static JFXButton confirm;
    private static JFXButton cancel;

    static {
        cancel = new JFXButton("Cancel");
        cancel.getStyleClass().add("button-close-small");
        cancel.setButtonType(JFXButton.ButtonType.RAISED);

        confirm = new JFXButton("Ok");
        confirm.getStyleClass().add("button-check-small");
        confirm.setButtonType(JFXButton.ButtonType.RAISED);
    }


    public DialogUtil(StackPane pane) {
        this.pane = pane;
    }

    public void showDialogConfirm(String message, Runnable onConfirm, Runnable onDismiss) {
        JFXDialogLayout confirmDialogContent = createDialogLayout("Confirm", message);
        JFXDialog confirmDialog = createConfirmDialog(onConfirm, onDismiss, confirmDialogContent);
        confirmDialog.show();
        Platform.runLater(() -> cancel.requestFocus());
    }
        public void showDialogInfo(String message) {
        JFXDialogLayout confirmDialogContent = createDialogLayout("Info", message);
        JFXDialog confirmDialog = createConfirmDialog(() -> {}, () -> {}, confirmDialogContent);
        confirmDialog.show();
    }

    public void showDialogWarn(String message) {
        JFXDialogLayout confirmDialogContent = createDialogLayout("Warning", message);
        JFXDialog confirmDialog = createConfirmDialog(() -> {}, () -> {}, confirmDialogContent);
        confirmDialog.show();
    }

    public void showDialogError(String message, Throwable e) {
        JFXDialogLayout confirmDialogContent = createDialogLayout("Error", message + "\n\nError: " + e.getMessage());
        JFXDialog confirmDialog = createConfirmDialog(() -> {}, () -> {}, confirmDialogContent);
        confirmDialog.show();
    }
    private JFXDialogLayout createDialogLayout(String header, String message) {
        JFXDialogLayout confirmDialogContent = new JFXDialogLayout();
        confirmDialogContent.setActions(cancel, confirm);
        confirmDialogContent.setHeading(new Text(header));
        confirmDialogContent.setBody(new Text(message));
        return confirmDialogContent;
    }

    private JFXDialog createConfirmDialog(Runnable onConfirm, Runnable onDismiss, JFXDialogLayout confirmDialogContent) {
        JFXDialog confirmDialog = new JFXDialog(pane, confirmDialogContent, JFXDialog.DialogTransition.BOTTOM);

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                onDismiss.run();
                confirmDialog.close();
                pane.prefWidthProperty().unbind();
            }
        });

        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                onConfirm.run();
                confirmDialog.close();
                pane.prefWidthProperty().unbind();
            }
        });
        return confirmDialog;
    }
}
