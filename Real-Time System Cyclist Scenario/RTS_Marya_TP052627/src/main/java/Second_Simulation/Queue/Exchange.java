package Second_Simulation.Queue;

import Second_Simulation.sharedExchange;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
//PRODUCER SIDE
public class Exchange { //EXCHANGE CLASS TO TRANSFER THE RESPONSE BODY VIA RABBIT MQ
    public void TransferQ(String msg, ConnectionFactory factory){
        sharedExchange shared = new sharedExchange(); //INITIALIZING SHAREDEXCHANGE CLASS

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
//                channel.exchangeDeclare("data", "direct");
//                channel.exchangeDeclare("data1","direct");
//                channel.exchangeDeclare("data2","direct");
            channel.exchangeDeclare(shared.EXCHANGE_NAME,"direct"); //PASSING SHARED EXCHANGE NAME
            // channel.exchangeDeclare(shared.EXCHANGE_NAME,"direct");
            // channel.exchangeDeclare(shared.EXCHANGE_NAME, "direct");
            //channel.exchangeDeclare(ExchangeName2,"fanout");
            //city = binding name
            channel.basicPublish(shared.EXCHANGE_NAME, "city", false, null, msg.getBytes());
            // channel.basicPublish(shared.EXCHANGE_NAME, "city1", false, null, msg.getBytes());
            // channel.basicPublish(shared.EXCHANGE_NAME, "city2", false, null, msg.getBytes());

        }catch(Exception e){}

    }
}


