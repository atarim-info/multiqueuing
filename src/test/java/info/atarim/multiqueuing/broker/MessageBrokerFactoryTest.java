/**
 * 
 */
package info.atarim.multiqueuing.broker;

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

/**
 * @author vladimir
 *
 */
class MessageBrokerFactoryTest {
	private static final Logger LOG = LoggerFactory.getLogger(MessageBrokerFactoryTest.class );

	private static final String ALICE_QUEUE = "ALICE_QUEUE";
	private static final String BOB_QUEUE = "BOB_QUEUE";

	/**
	 * Test method for {@link info.atarim.multiqueuing.broker.MessageBrokerFactory#createMessageBroker(int)}.
	 * @throws Exception 
	 */
	@Test
	void testMessageBroker() throws Exception {
		int threadCount = 5;
			
		MessageBroker<String> messageBroker = MessageBrokerFactory.createMessageBroker(threadCount);
		assertNotNull(messageBroker);
		
		// Alice submits 1000 requests every minute
		// Bob submits 5 requests every second
		
		UserData<String> aliceData = new UserData<>("ALICE", "192.168.1.101");
		UserData<String> bobData = new UserData<>("BOB", "192.168.1.102");
		
		messageBroker.start();

		messageBroker.createQueue(ALICE_QUEUE);
		messageBroker.createQueue(BOB_QUEUE);
		
		Thread alice = createPublisherThread(messageBroker, aliceData, ALICE_QUEUE, 1000, 60000, 1);
		Thread bob = createPublisherThread(messageBroker, bobData, BOB_QUEUE, 5, 5000, 12);
		
		CountDownLatch aliceCountDownLatch = createConsumer(messageBroker, ALICE_QUEUE, 1000);
		CountDownLatch bobCountDownLatch = createConsumer(messageBroker, BOB_QUEUE, 5*12);
		
		alice.start();
		bob.start();
		
		assertCountDown(ALICE_QUEUE, aliceCountDownLatch);
		assertCountDown(BOB_QUEUE, bobCountDownLatch);
		messageBroker.stop();
	}

	private void assertCountDown(String queueName, final CountDownLatch countDownLatch) throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() { 
				try {
					assertTrue(countDownLatch.await(100, TimeUnit.SECONDS), "Missing messages from QUEUE: " + queueName);
				} catch (InterruptedException e) {
					LOG.debug(e.getMessage());
				}
			}
		});
		thread.start();
		thread.join();
	}

	/**
	 * createConsumer
	 * @param messageBroker
	 * @param queueName
	 * @param messageCount
	 * @return
	 * @throws ConsumerAlreadyExistsException
	 * @throws QueueNotExistsException
	 * @throws QueueAlreadyExistsException
	 */
	private CountDownLatch createConsumer(MessageBroker<String> messageBroker, String queueName, int messageCount) throws ConsumerAlreadyExistsException, QueueNotExistsException, QueueAlreadyExistsException {
		final CountDownLatch countDownLatch  = new CountDownLatch(messageCount);
		MessageConsumer<String> dummyConsumer = new MessageConsumer<String>() {
			@Override
			public void accept(Message<String> message) {
				LOG.debug("Message Received, payload: " +  message.messagePayload);
				countDownLatch.countDown();
			}
		};
		messageBroker.subcribe(queueName, dummyConsumer);	
		return countDownLatch;
	}
	
	/**
	 * createPublisherThread
	 * @param messageBroker
	 * @param userData
	 * @param queueName
	 * @param messageCont
	 * @param sleepTime
	 * @param repeatTimes
	 * @return
	 */
	private Thread createPublisherThread(MessageBroker<String> messageBroker, UserData<String> userData, String queueName, int messageCont, long sleepTime, int repeatTimes) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int t = repeatTimes; t > 0; t--) {
					long startTime = System.currentTimeMillis();
					for (int i = 0; i < messageCont; i++) {
						Message<String> message = new Message<>(userData, "Message_" + i);
						try {
							messageBroker.offer(queueName, message);
						} catch (QueueNotExistsException e) {
							LOG.error(e.getMessage(), e);
						}
					}
					long endTime = System.currentTimeMillis();
					long duration = endTime - startTime;
					LOG.debug(messageCont +  " Messages sent to QUEUE: " + queueName + " in " + duration + " miliseconds");
					
					if (t > 1) {
						long time2sleep = sleepTime - duration;
						LOG.debug("Consumer QUEUE: " + queueName + " going to sleep " + time2sleep + " miliseconds");
						try {
							Thread.sleep(time2sleep);
						} catch (InterruptedException e) {
							LOG.debug(e.getMessage(), e);
						} 
					}
				}
			}
		});
		return thread;
	}

}
