package br.com.string.consumer;

import com.rabbitmq.client.*;

public class ConsumerContinuos {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // Criar uma conexão e canal
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Endereço do RabbitMQ
        factory.setUsername("user");  // Nome do usuário
        factory.setPassword("password");  // Senha do usuário
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declarar a fila (mesmo nome que no Producer)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press Ctrl+C");

            // Consumir mensagens de forma contínua
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            // Iniciar o consumo da fila e continuar no loop
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

            // Manter o consumidor em execução (não deixar a aplicação terminar)
            while (true) {
                // O programa ficará em execução esperando por novas mensagens
                Thread.sleep(1000); // Adiciona um pequeno delay para evitar o uso excessivo de CPU
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

