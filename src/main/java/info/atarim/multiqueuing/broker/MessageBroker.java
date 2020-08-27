package info.atarim.multiqueuing.broker;

import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

public interface MessageBroker<T> {

	MessageQueue<T> subcribe(String queueName, MessageConsumer<T> consumer)
			throws ConsumerAlreadyExistsException, QueueNotExistsException, QueueAlreadyExistsException;

	MessageQueue<T> createQueue(String name) throws QueueAlreadyExistsException;

	int getQueuesCount();

	void stop();

	void start();

	void offer(String queueName, Message<T> message) throws QueueNotExistsException;

}