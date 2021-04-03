package ch.bytecrowd.kafkazen.gui.views;

public enum FxmlView {

    HOME("view/fxml/home/home.fxml"),
    MENU("view/fxml/navigation/menu.fxml"),
    EDIT("view/fxml/entities/edit.fxml"),
    TABLE("view/fxml/entities/table.fxml"),
    KAFKA_PUBLISHER("view/fxml/publisher/publisher.fxml"),
    KAFKA_CONSUMER("view/fxml/consumer/consumer.fxml"),
    KAFKA_ADMINISTRATION("view/fxml/administration/administration.fxml")
    ;

    private final String resourcePath;

    FxmlView(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
