package info.atarim.multiqueuing.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.atarim.multiqueuing.concurrent.ThreadPool;
import info.atarim.multiqueuing.exceptions.ConsumerAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueAlreadyExistsException;
import info.atarim.multiqueuing.exceptions.QueueNotExistsException;

public class QueueManagerImpl<T> implements QueueManager<T> {
	private static final Logger LOG = LoggerFactory.getLogger(QueueManagerImpl.class );
	
	private static final int DEFAULT_THREAD_COUNT = 5;
	
	private final Map<String, UUID> queueName2IDMap;
	private final Map<UUID, MessageQueue<T>> queueMap;
	private final Map<UUID, MessageConsumer<T>> queueConsumersMap;
	
	private final ThreadPool threadPool;
	private boolean stop = true;
	
	/**
	 * QueueManagerImpl Constructor
	 */
	public QueueManagerImpl() {
		this(ThreadPool.createThreadPoll(DEFAULT_THREAD_COUNT));
	}
	
	/**
	 * QueueManagerImpl Constructor
	 * @param threadPool
	 */
	public QueueManagerImpl(ThreadPool threadPool) {
		this.threadPool = threadPool;
		this.queueName2IDMap = new HashMap<>();
		this.queueMap = new HashMap<>();
		this.queueConsumersMap = new HashMap<>();			
		
	}
	
	@Override
	public void start() {
		stop = false;
		final Thread parentThread = Thread.currentThread();
		threadPool.runInThread(parentThread, new Runnable() {
			@Override
			public void run() {
				do {
					for (UUID queueId : queueConsumersMap.keySet()) {
						MessageQueue<T> messageQueue = queueMap.get(queueId);
						if (!messageQueue.isEmpty()) {
							//Run in thread
							retriveMessages(queueId, messageQueue);
						}
					}
					//
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						LOG.debug("Sleep Interupted", e);
					}
				} while (!stop);  
			}

			

		}, "Go over Queues");
	}
	
	@Override
	public void stop() {
		stop = true;
	}
	
	private void retriveMessages(UUID queueId, MessageQueue<T> messageQueue) {
		final Thread parentThread = Thread.currentThread();
		threadPool.runInThread(parentThread, new Runnable() {
			@Override
			public void run() {
				Message<T> message = messageQueue.poll();
				MessageConsumer<T> consumer = queueConsumersMap.get(queueId);
				consumer.accept(message);
			}
		}, "thread - Retrive messages fom Queue: "  + messageQueue.getName()); 
				
	}
	
	@Override
	public MessageQueue<T> createQueue(String name) throws QueueAlreadyExistsException {		
		if (queueName2IDMap.containsKey(name)) {
			throw new QueueAlreadyExistsException(name);
		}
		MessageQueue<T> messageQueue = new MessageQueueImpl<>(name);
		UUID queueId = messageQueue.getQueueId(); 
		queueName2IDMap.put(name, queueId);
		queueMap.put(queueId, messageQueue);
		LOG.debug("MessageQueue Created, name = {}, queueId = {}", name, queueId);
		return messageQueue;
	}
		
	@Override
	public MessageQueue<T> getQueue(String queueName) {
		MessageQueue<T> messageQueue = null;
		UUID queueId = queueName2IDMap.get(queueName);
		if (queueId != null) {
			messageQueue = queueMap.get(queueId);
		}
		return messageQueue;
	}
	
	@Override
	public MessageConsumer<T> getConsumer(String queueName) {
		MessageConsumer<T> messageConsumer = null;
		UUID queueId = queueName2IDMap.get(queueName);
		if (queueId != null) {
			messageConsumer = queueConsumersMap.get(queueId);
		}
		return messageConsumer;
	}
	
	@Override
	public boolean isQueueHasConsumer(String queueName ) {
		MessageConsumer<T> consumer = getConsumer(queueName);
		return consumer != null;
	}
	
	@Override
	public int getQueuesCount() {
		return queueName2IDMap.size();
	}

	@Override
	public void offer(String queueName, Message<T> message) throws QueueNotExistsException {
		MessageQueue<T> messageQueue = getQueue(queueName);
		if (messageQueue != null) {
			messageQueue.offer(message);
		}
		else {
			throw new QueueNotExistsException(queueName);			
		}
	}
	
	@Override
	public void setConsumer(final String queueName, final MessageConsumer<T> consumer) throws ConsumerAlreadyExistsException, QueueNotExistsException {
		UUID queueId = queueName2IDMap.get(queueName);
		if (queueId == null) {
			throw new QueueNotExistsException(queueName);
		}
		
		if (isQueueHasConsumer(queueName)) {
			throw new ConsumerAlreadyExistsException(queueName);
		}
		
		if (queueId != null) {
			queueConsumersMap.put(queueId, consumer);
		}
		
	}

}
