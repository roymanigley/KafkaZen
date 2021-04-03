package ch.bytecrowd.kafkazen.gui.utils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Constants {

    public static final SimpleStringProperty PRESELECTED_KAFKA_SERVER = new SimpleStringProperty("localhost:9092");
    public static final SimpleStringProperty VALID_KAFKA_SERVER = new SimpleStringProperty();
    public static final SimpleBooleanProperty IS_CONSUMER_RUNNING = new SimpleBooleanProperty(false);
}
