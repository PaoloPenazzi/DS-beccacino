package com.example.beccaccino;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.ForgivingExceptionHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Utilities {

    public static Connection createConnection() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setExceptionHandler(new ForgivingExceptionHandler());


        factory.setUsername("user");
        factory.setPassword("user");
        factory.setVirtualHost("/");

        // not "localhost" because i'm in the container
        // factory.setHost("localhost");
        // factory.setHost(System.getenv("RABBIT_HOST"));
        // NO PERCHÈ SIAMO DENTRO AL 'CONTAINER' DELL'EMULATORE QUINDI DOBBIAMO METTERE L'IP DIRETTAMENTE
        // factory.setHost("rabbitmq");
        // IP da app sul telefono
        //factory.setHost("192.168.1.5");
        // IP da emulathor
        factory.setHost("10.0.2.2");
        factory.setPort(5672);

        factory.setAutomaticRecoveryEnabled(true);
        while (true) {
            try {
                System.out.println("Connection established");
                return factory.newConnection();
            } catch (java.net.ConnectException e) {
                System.out.println("Retrying connection to RabbitMQ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void createQueue(final Channel channel, final String exchangeName, final BuiltinExchangeType exchangeType,
                                   final String nameQueue, boolean durable, boolean exclusive, boolean autoDelete,
                                   Map<String, Object> arguments, String routingKey) throws IOException {
        channel.queueDeclare(nameQueue, durable, exclusive, autoDelete, arguments);
        channel.exchangeDeclare(exchangeName, exchangeType);
        channel.queueBind(nameQueue, exchangeName, routingKey);
    }

}
