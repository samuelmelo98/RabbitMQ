package br.com.string.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.*;

public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // Criar uma conexão e canal
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Endereço do RabbitMQ
        factory.setUsername("user");  // Seu nome de usuário configurado no RabbitMQ
        factory.setPassword("password");  // Sua senha configurada no RabbitMQ
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declarar a fila (mesmo nome que no Producer)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press Ctrl+C");

            // Consumir mensagens
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            // Iniciar o consumo da fila
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        }
    }
}
