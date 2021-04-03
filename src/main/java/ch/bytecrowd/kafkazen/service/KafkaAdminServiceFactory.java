package ch.bytecrowd.kafkazen.service;

public class KafkaAdminServiceFactory {

    public static KafkaAdminService createService(String host) {
        return new KafkaAdminServiceImpl(host);
    }
}
