package info.atarim.multiqueuing.broker;

import info.atarim.multiqueuing.concurrent.*;
import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

public class MessageBrokerImpl<T> implements MessageBroker<T> {
	private ThreadPool threadPool;
	private QueueManager<T> queueManager;

	/**
	 * @param threadPool
	 */
	public MessageBrokerImpl(ThreadPool threadPool) {
		this.threadPool = threadPool;
		this.queueManager = new QueueManagerImpl<T>(threadPool);
	}
	
	
	@Override
	public MessageQueue<T> subcribe(String queueName, MessageConsumer<T> consumer) throws ConsumerAlreadyExistsException, QueueNotExistsException, QueueAlreadyExistsException {
		MessageQueue<T> messageQueue = queueManager.getQueue(queueName);
		if (messageQueue == null) {
			queueManager.createQueue(queueName);
		}
		queueManager.setConsumer(queueName, consumer);
		return messageQueue;		
	}


	@Override
	public MessageQueue<T> createQueue(String name) throws QueueAlreadyExistsException {
		return queueManager.createQueue(name);
	}


	@Override
	public int getQueuesCount() {
		return queueManager.getQueuesCount();
	}


	@Override
	public void stop() {
		queueManager.stop();
	}


	@Override
	public void start() {
		queueManager.start();
	}

	@Override
	public void offer(String queueName, Message<T> message) throws QueueNotExistsException {
		queueManager.offer(queueName, message);
	}		

}
