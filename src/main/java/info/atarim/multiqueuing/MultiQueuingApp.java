/**
 * 
 */
package info.atarim.multiqueuing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.atarim.multiqueuing.broker.Message;
import info.atarim.multiqueuing.broker.MessageBroker;
import info.atarim.multiqueuing.broker.MessageBrokerFactory;
import info.atarim.multiqueuing.broker.MessageConsumer;
import info.atarim.multiqueuing.data.UserData;
import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

/**
 * @author vladimir
 *
 */
public class MultiQueuingApp {
	private static final Logger LOG = LoggerFactory.getLogger(MultiQueuingApp.class );
	
	private static final String ALICE_QUEUE = "ALICE_QUEUE";
	private static final String BOB_QUEUE = "BOB_QUEUE";

	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		int threadCount = 5;
		
		try {
			MessageBroker<String> messageBroker = MessageBrokerFactory.createMessageBroker(threadCount);
			
			// Alice submits 1000 requests every minute
			// Bob submits 5 requests every second
			
			UserData<String> aliceData = new UserData<>("ALICE", "192.168.1.101");
			UserData<String> bobData = new UserData<>("BOB", "192.168.1.102");
			
			messageBroker.start();

			messageBroker.createQueue(ALICE_QUEUE);
			messageBroker.createQueue(BOB_QUEUE);
			
			MessageConsumer<String> aliceConsumer = createConsumer(messageBroker, ALICE_QUEUE, 1000);
			MessageConsumer<String> bobConsumero = createConsumer(messageBroker, BOB_QUEUE, 5*12);

			Thread alice = createPublisherThread(messageBroker, aliceData, ALICE_QUEUE, 1000, 60000, 1);
			Thread bob = createPublisherThread(messageBroker, bobData, BOB_QUEUE, 5, 5000, 12);
			
			alice.start();
			bob.start();
			
			// wait for all messages being sent
			alice.join();
			LOG.debug("Alice sent all his messages");
			
			bob.join();
			LOG.debug("Bob sent all his messages");
			
			Thread.sleep(1000);
			
			messageBroker.stop();
			
		} catch (QueueAlreadyExistsException e) {
			LOG.error(e.getMessage(), e);
		} catch (ConsumerAlreadyExistsException e) {
			LOG.error(e.getMessage(), e);
		} catch (QueueNotExistsException e) {
			LOG.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.debug(e.getMessage(), e);
		}	
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
	private static MessageConsumer<String> createConsumer(MessageBroker<String> messageBroker, String queueName, int messageCount) throws ConsumerAlreadyExistsException, QueueNotExistsException, QueueAlreadyExistsException {
		MessageConsumer<String> dummyConsumer = new MessageConsumer<String>() {
			@Override
			public void accept(Message<String> message) {
				LOG.debug("Message Received, payload: " +  message.getMessagePayload());
			}
		};
		messageBroker.subcribe(queueName, dummyConsumer);	
		return dummyConsumer;
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
	private static Thread createPublisherThread(MessageBroker<String> messageBroker, UserData<String> userData, String queueName, int messageCont, long sleepTime, int repeatTimes) {
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
						LOG.debug("Publisher to QUEUE: " + queueName + " going to sleep " + time2sleep + " miliseconds");
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
