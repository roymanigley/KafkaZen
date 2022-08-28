package ch.bytecrowd.kafkazen.service;

import ch.bytecrowd.kafkazen.service.impl.KafkaAdminServiceImpl;

public class KafkaAdminServiceFactory {

    public static KafkaAdminService createService(String host) {
        return new KafkaAdminServiceImpl(host);
    }
}
