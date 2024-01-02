package notification1;

import java.time.LocalDateTime;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class NotificationConsumer1 {

	public static void main(String ag[]) {

		ConnectionFactory connectionFactory = getConnectionFactory();
		placeOrderEvent(connectionFactory);
	}

	private static ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("localhost");
		connectionFactory.setPort(5672);
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		return connectionFactory;
	}

	private static void placeOrderEvent(ConnectionFactory connectionFactory) {
		System.out.println("placeOrderEvent method start");
		receiveEvent(connectionFactory, "place-order-exchange", "fanout", "place-order-queue");
		System.out.println("placeOrderEvent method end");
	}

	private static void receiveEvent(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			String queue) {
		try (Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel();) {

			channel.exchangeDeclare(exchange, exchangeType, false);
			channel.queueDeclare(queue, false, false, false, null);
			channel.queueBind(queue, exchange, "");

			while (true) {
				GetResponse response = channel.basicGet(queue, true);

				if (response != null) {
					String message = new String(response.getBody(), "UTF-8");
					System.out.println("Timestamp --" + LocalDateTime.now());
					System.out.println("Notification1 Place Order Event Received '" + message + "'");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
