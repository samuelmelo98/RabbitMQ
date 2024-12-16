package br.com.string.producer;

import br.com.string.model.Pessoa;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.time.Instant;

public class ProducerEnviandoJsonNewThread implements Runnable {

    private final Channel channel;
    private final String QUEUE_NAME;

    public ProducerEnviandoJsonNewThread(Channel channel, String QUEUE_NAME) {
        this.channel = channel;
        this.QUEUE_NAME = QUEUE_NAME;
    }

    @Override
    public void run() {
        // Criar o objeto que será enviado
        // Pessoa pessoa = new Pessoa("Samuel Anderson", "01472982100");
        System.err.println("INICIOU A THREAD ProducerEnviandoJsonNewThread!");
        try {
            for (int i = 0; i < 6; i++) {

//                    long timestamp = System.nanoTime();  // Obtém um valor de alta precisão com base no tempo em nanossegundos
//                    Instant instant = Instant.ofEpochSecond(0, timestamp); // Usa o timestamp de nano segundos
                // Instant instant = Instant.now().plusMillis(i * 1000); // Incrementa 100 milissegundos a cada iteração
                // Usando System.currentTimeMillis para gerar um timestamp crescente
                Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + i);
                Pessoa pessoa = new Pessoa("Nome:".concat(String.valueOf(i)), "CPF-".concat(String.valueOf(i)), instant);

                // Converter o objeto para JSON usando Jackson
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule()); // Registro do módulo JavaTimeModule
                objectMapper.findAndRegisterModules(); // Também registra outros módulos, se necessário
                String jsonMessage = objectMapper.writeValueAsString(pessoa);

                // Enviar a mensagem JSON
                channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
                System.out.println(" [x] Sent '" + jsonMessage + "'");
                pessoa = null;
                Thread.sleep(1000);
            }
            System.err.println("FINALIZOU A THREAD SECUNDARIA!");
        }
        catch(Throwable e){
            e.printStackTrace();
        }

    }
    public static void main(String[] argv) throws Exception {
        System.err.println("INICIOU A THREAD PRINCIPAL!");
        // Aqui, você cria a sua conexão com o RabbitMQ e passa o canal e nome da fila
        // Para fins de exemplo, substitua com sua implementação de canal e nome de fila reais.

        Channel channel = null; // Inicialize seu canal RabbitMQ corretamente
        String QUEUE_NAME = "hello";  // Substitua pelo nome da fila
        String NAME_THREAD= "Nome_da_thread";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Endereço do RabbitMQ
        factory.setUsername("user");  // Nome do usuário
        factory.setPassword("password");  // Senha do usuário
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        // Declarar a fila
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Criando uma nova thread para rodar o loop
        Thread producerThread = new Thread(new ProducerEnviandoJsonNewThread(channel, QUEUE_NAME), NAME_THREAD);
        // Definindo o nome da thread
        producerThread.setName("Producer-Thread");
        producerThread.start(); // Inicia a execução do código dentro da thread

        try {
            // A thread principal aguarda a thread 'producerThread' terminar
            producerThread.join(); // Espera a thread Producer-Thread terminar antes de continuar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // A thread principal só continuará aqui após a thread 'producerThread' terminar
        System.out.println("Thread principal: A thread secundária terminou.");
         System.exit(1);

    }
}
