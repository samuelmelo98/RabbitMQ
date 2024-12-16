package br.com.string.producer;

import br.com.string.model.Pessoa;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class ProducerEnviandoJsonNewThread2 {
    public static void main(String[] args) {
        System.out.println("INICIOU A THREAD PRINCIPAL!");
        // Criação da thread secundária
        Thread threadSecundaria = new Thread(new Runnable() {
            Channel channel = null; // Inicialize seu canal RabbitMQ corretamente
            Connection connection = null; // Inicialize a conexão com RabbitMQ
            String QUEUE_NAME = "hello";  // Substitua pelo nome da fila
            @Override
            public void run() {
                try {
                    System.out.println("INICIOU A THREAD ProducerEnviandoJsonNewThread2!");
                    String NAME_THREAD = "Nome_da_thread";

                    // Configuração da conexão com RabbitMQ
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost("localhost");  // Endereço do RabbitMQ
                    factory.setUsername("user");  // Nome do usuário
                    factory.setPassword("password");  // Senha do usuário

                    // Conectar ao RabbitMQ
                    connection = factory.newConnection(); // Pode lançar TimeoutException
                    channel = connection.createChannel(); // Pode lançar IOException
                    // Declara a fila para garantir que ela existe
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    // Simulando o envio de mensagens
                    for (int i = 0; i < 6; i++) {
                        try {
                            Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + i);
                            Pessoa pessoa = new Pessoa("Nome:".concat(String.valueOf(i)), "CPF-".concat(String.valueOf(i)), instant);
                            // Converter o objeto para JSON
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new JavaTimeModule()); // Registro do módulo JavaTimeModule
                            objectMapper.findAndRegisterModules();
                            String jsonMessage = objectMapper.writeValueAsString(pessoa);
                            // Enviar a mensagem JSON
                            channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
                            System.out.println(" [x] Sent '" + jsonMessage + "'");
                            pessoa = null;
                            // Simulando um delay (descomente se necessário)
                            Thread.sleep(1000);  // Isso pode lançar InterruptedException, então aqui é tratado
                        } catch (InterruptedException e) {
                            // Trata a exceção InterruptedException de forma específica
                            e.printStackTrace();
                            Thread.currentThread().interrupt();  // Se a thread for interrompida, podemos interromper também
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();  // Trate a exceção TimeoutException aqui
                } finally {
                    // Fechando recursos após o uso
                    try {
                        if (channel != null) {
                            channel.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (IOException | TimeoutException e) {
                        e.printStackTrace();
                    }
                    System.out.println("FINALIZOU A THREAD SECUNDARIA!");
                }
            }
        });
        // Inicia a thread secundária
        threadSecundaria.start();
        // A thread principal aguarda a conclusão da thread secundária
        try {
            threadSecundaria.join();  // Espera até que a threadSecundaria termine
        } catch (InterruptedException e) {
            e.printStackTrace();  // Caso a thread principal seja interrompida, você pode tratá-la aqui
        }
        // Quando a thread secundária terminar, a thread principal continuará
        System.out.println("Thread principal: A thread secundária terminou.");
    }
}
