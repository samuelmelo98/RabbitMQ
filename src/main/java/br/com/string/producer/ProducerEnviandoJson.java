package br.com.string.producer;
import br.com.string.model.Pessoa;
import com.rabbitmq.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;

public class ProducerEnviandoJson {

        private final static String QUEUE_NAME = "hello";

        public static void main(String[] argv) throws Exception {
            // Criar a conexão e o canal
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");  // Endereço do RabbitMQ
            factory.setUsername("user");  // Nome do usuário
            factory.setPassword("password");  // Senha do usuário
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                // Declarar a fila
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                // Criar o objeto que será enviado
               // Pessoa pessoa = new Pessoa("Samuel Anderson", "01472982100");
                for(int i=0; i < 6; i++) {
//                    long timestamp = System.nanoTime();  // Obtém um valor de alta precisão com base no tempo em nanossegundos
//                    Instant instant = Instant.ofEpochSecond(0, timestamp); // Usa o timestamp de nano segundos
                   // Instant instant = Instant.now().plusMillis(i * 1000); // Incrementa 100 milissegundos a cada iteração
                    // Usando System.currentTimeMillis para gerar um timestamp crescente
                    Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + i);
                    Pessoa pessoa = new Pessoa("Nome:".concat(String.valueOf(i)),"CPF-" .concat(String.valueOf(i)), instant);

                    // Converter o objeto para JSON usando Jackson
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule()); // Registro do módulo JavaTimeModule
                    objectMapper.findAndRegisterModules(); // Também registra outros módulos, se necessário
                    String jsonMessage = objectMapper.writeValueAsString(pessoa);

                    // Enviar a mensagem JSON
                    channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
                    System.out.println(" [x] Sent '" + jsonMessage + "'");
                    pessoa= null;
                    Thread.sleep(1000);


                }
            }
        }
    }
