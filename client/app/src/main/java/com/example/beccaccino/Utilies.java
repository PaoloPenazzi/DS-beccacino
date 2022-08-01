package com.example.beccaccino;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Utilies {

    public static Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("user");
        factory.setPassword("user");
        factory.setVirtualHost("/");
        // not "localhost" because i'm in the container
        // factory.setHost("localhost");
        factory.setHost(System.getenv("RABBIT_HOST"));
        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(true);
        while(true) {
            try {
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

    public static String createReceiveQueue(final Channel channel, final String nameQueue, final boolean durable, final boolean exclusive,
                                            final boolean autoDelete, Map<String, Object> arguments) throws IOException {
        channel.queueDeclare(nameQueue, durable, exclusive, autoDelete, arguments);
        return nameQueue;
    }

    public static String createSendQueue(final Channel channel, final String exchangeName, final BuiltinExchangeType exchangeType,
                                         final String routingKey, final String nameQueue, final boolean durable, final boolean exclusive,
                                         final boolean autoDelete, Map<String, Object> arguments) throws IOException {
        createReceiveQueue(channel, nameQueue, durable, exclusive, autoDelete, arguments);
        channel.exchangeDeclare(exchangeName, exchangeType);
        channel.queueBind(nameQueue, exchangeName, routingKey);
        return nameQueue;
    }

}
