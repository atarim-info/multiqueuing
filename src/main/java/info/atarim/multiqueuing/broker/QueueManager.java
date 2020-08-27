package info.atarim.multiqueuing.broker;

import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

public interface QueueManager<T> {

	/**
	 * createQueue
	 * @param name
	 * @return
	 * @throws QueueAlreadyExistsException
	 */
	MessageQueue<T> createQueue(String name) throws QueueAlreadyExistsException;

	/**
	 * getQueue
	 * @param name
	 * @return
	 */
	MessageQueue<T> getQueue(String name);

	/**
	 * offer
	 * @param queueName
	 * @param message
	 * @throws QueueNotExistsException
	 */
	void offer(String queueName, Message<T> message) throws QueueNotExistsException;

	/**
	 * getQueuesCount
	 * @return
	 */
	int getQueuesCount();

	/**
	 * setConsumer
	 * @param queueName
	 * @param consumer
	 * @throws ConsumerAlreadyExistsException
	 * @throws QueueNotExistsException 
	 */
	void setConsumer(String queueName, MessageConsumer<T> consumer) throws ConsumerAlreadyExistsException, QueueNotExistsException;

	/**
	 * @param queueName
	 * @return
	 */
	boolean isQueueHasConsumer(String queueName);

	/**
	 * getConsumer
	 * @param queueName
	 * @return
	 */
	MessageConsumer<T> getConsumer(String queueName);

	void stop();

	void start();

}