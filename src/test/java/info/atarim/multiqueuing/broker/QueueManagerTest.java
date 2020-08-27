package info.atarim.multiqueuing.broker;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.atarim.multiqueuing.data.UserData;
import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

class QueueManagerTest {
	private static final Logger LOG = LoggerFactory.getLogger(QueueManagerImpl.class );

	@Test
	void testCreateQueue() throws Exception {
		String queueName = "QUEUE1";
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		MessageQueue<String> messageQueue = queueManager.createQueue(queueName);
		assertNotNull(messageQueue);
		assertEquals(queueName, messageQueue.getName());
	}
	
	@Test
	void testCreateMultipleQueues() throws Exception {
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		int queueCount = 100;
		for (int i = 0; i < queueCount; i++) {
			String queueName = "QUEUE" + i;
			queueManager.createQueue(queueName);		
		}
		
		assertEquals(queueCount, queueManager.getQueuesCount());
	}

	@Test
	void testGetQueue() throws Exception {
		String queueName = "QUEUE1";
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.createQueue(queueName);
		
		MessageQueue<String> messageQueue = queueManager.getQueue(queueName); 
		assertNotNull(messageQueue);
		assertEquals(queueName, messageQueue.getName());
	}
	
	@Test
	void testThrowQueueAlreadyExistsException() throws Exception {
		String queueName = "QUEUE1";
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.createQueue(queueName);
		try {
			queueManager.createQueue(queueName);
		} catch (Exception e) {
			assertThatExceptionOfType(QueueAlreadyExistsException.class);
		}
	}
	
	@Test
	void testOffer() throws Exception {
		String queueName = "QUEUE1";
		UserData<String> userData = new UserData<>("user_001", "192.168.1.101");
		
		Message<String> message = new Message<>(userData, "Message 1");
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.createQueue(queueName);
		queueManager.offer(queueName, message);
	}
	
	@Test
	void testOfferAndRetrive() throws Exception {
		String queueName = "QUEUE1";
		UserData<String> userData = new UserData<>("user_001", "192.168.1.101");
		
		final String messagePayload = "Message 1";
		Message<String> message = new Message<>(userData, messagePayload);
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.start();
		queueManager.createQueue(queueName);
		queueManager.offer(queueName, message);
		
		final CountDownLatch receivedMessage  = new CountDownLatch(1);
		
		MessageConsumer<String> dummyConsumer = new MessageConsumer<String>() {
			@Override
			public void accept(Message<String> message) {
				LOG.debug("Message Received, payload: " +  message.messagePayload);
				assertEquals(messagePayload, message.messagePayload);
				receivedMessage.countDown();
			}
		};
		
		queueManager.setConsumer(queueName, dummyConsumer);		
		
		assertTrue(receivedMessage.await(2, TimeUnit.SECONDS));
		queueManager.stop();
	}
	
	@Test
	void testThrowOfferQueueNotExistsException() throws Exception {
		String queueName = "QUEUE1";
		UserData<String> userData = new UserData<>("user_001", "192.168.1.101");
		
		Message<String> message = new Message<>(userData, "Message 1");
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		//queueManager.createQueue(queueName);
		try {
			queueManager.offer(queueName, message);
		} catch (Exception e) {
			assertThatExceptionOfType(QueueNotExistsException.class);
		} 
	}
	@Test
	void testThrowOfferQueueAreadyExistsException() throws Exception {
		String queueName = "QUEUE1";
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.createQueue(queueName);
		
		try {
			queueManager.createQueue(queueName);
		} catch (Exception e) {
			assertThatExceptionOfType(QueueAlreadyExistsException.class);
		} 
	}
	
	@Test
	void testThrowConsumerAlreadyExistsException() throws Exception {
		String queueName = "QUEUE1";
		
		QueueManager<String> queueManager = new QueueManagerImpl<String>();
		queueManager.createQueue(queueName);
		
		MessageConsumer<String> dummyConsumer = new MessageConsumer<String>() {
			@Override
			public void accept(Message<String> message) {
			}
		};
		queueManager.setConsumer(queueName, dummyConsumer);
		
		try {
			
			queueManager.setConsumer(queueName, dummyConsumer);
		} catch (Exception e) {
			assertThatExceptionOfType(ConsumerAlreadyExistsException.class);
		} 
	}

}
