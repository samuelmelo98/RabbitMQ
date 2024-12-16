package br.com.string.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // Criar uma conexão e canal

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Endereço do RabbitMQ, pode ser 'localhost' ou o IP do servidor
        factory.setUsername("user");  // Seu nome de usuário configurado no RabbitMQ
        factory.setPassword("password");  // Sua senha configurada no RabbitMQ
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declarar uma fila
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello RabbitMQ!";

            // Enviar mensagem
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}


