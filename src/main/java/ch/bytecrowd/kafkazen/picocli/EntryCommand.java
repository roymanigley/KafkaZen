package ch.bytecrowd.kafkazen.picocli;

import ch.bytecrowd.kafkazen.Main;
import ch.bytecrowd.kafkazen.gui.utils.Constants;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true)
public class EntryCommand implements Runnable{

    static {
        System.out.println("\n" +
                "\t ██ ▄█▀▄▄▄        █████▒██ ▄█▀▄▄▄      ▒███████▒▓█████  ███▄    █ \n" +
                "\t ██▄█▒▒████▄    ▓██   ▒ ██▄█▒▒████▄    ▒ ▒ ▒ ▄▀░▓█   ▀  ██ ▀█   █ \n" +
                "\t▓███▄░▒██  ▀█▄  ▒████ ░▓███▄░▒██  ▀█▄  ░ ▒ ▄▀▒░ ▒███   ▓██  ▀█ ██▒\n" +
                "\t▓██ █▄░██▄▄▄▄██ ░▓█▒  ░▓██ █▄░██▄▄▄▄██   ▄▀▒   ░▒▓█  ▄ ▓██▒  ▐▌██▒\n" +
                "\t▒██▒ █▄▓█   ▓██▒░▒█░   ▒██▒ █▄▓█   ▓██▒▒███████▒░▒████▒▒██░   ▓██░\n" +
                "\t▒ ▒▒ ▓▒▒▒   ▓▒█░ ▒ ░   ▒ ▒▒ ▓▒▒▒   ▓▒█░░▒▒ ▓░▒░▒░░ ▒░ ░░ ▒░   ▒ ▒ \n" +
                "\t░ ░▒ ▒░ ▒   ▒▒ ░ ░     ░ ░▒ ▒░ ▒   ▒▒ ░░░▒ ▒ ░ ▒ ░ ░  ░░ ░░   ░ ▒░\n" +
                "\t░ ░░ ░  ░   ▒    ░ ░   ░ ░░ ░  ░   ▒   ░ ░ ░ ░ ░   ░      ░   ░ ░ \n" +
                "\t░  ░        ░  ░       ░  ░        ░  ░  ░ ░       ░  ░         ░ \n" +
                "\t                                       ░                          \n");
    }

    @CommandLine.Option(names = {"--host"}, description = "the host address (default=localhost:9092)", defaultValue = "localhost:9092")
    String host;

    public void run() {
        Constants.PRESELECTED_KAFKA_SERVER.setValue(host);
        Main.main(null);
    }
}
