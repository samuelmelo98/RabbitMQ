package br.com.string.consumer;
import br.com.string.model.Pessoa;
import com.rabbitmq.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;
public class ConsumerJson {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // Criar a conexão e o canal
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Endereço do RabbitMQ
        factory.setUsername("user");  // Nome do usuário
        factory.setPassword("password");  // Senha do usuário

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declarar a fila (mesmo nome que no Producer)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press Ctrl+C");

            // Consumir as mensagens
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // Converter o corpo da mensagem de bytes para String (JSON)
                String message = new String(delivery.getBody(), "UTF-8");

                // Converter o JSON para o objeto Java
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Pessoa pessoa = objectMapper.readValue(message, Pessoa.class);
                    System.out.println(" [x] Received '" + pessoa + "'");
                } catch (Exception e) {
                    System.err.println("Erro ao desserializar a mensagem: " + e.getMessage());
                }
            };

            // Iniciar o consumo da fila
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        }
    }
    }

