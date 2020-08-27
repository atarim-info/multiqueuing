package info.atarim.multiqueuing.broker;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class MessageQueueImpl<T> implements MessageQueue<T> {
	private String name;
	private UUID queueId;
	private Queue<Message<T>> messageQueue = new LinkedBlockingQueue<>();

	/**
	 * 
	 */
	public MessageQueueImpl(String name) {
		this.name = name;
		this.queueId = UUID.randomUUID();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getQueueId() {
		return queueId;
	}

	@Override
	public boolean offer(Message<T> e) {
		return messageQueue.offer(e);
	}

	@Override
	public Message<T> poll() {
		return messageQueue.poll();
	}

	@Override
	public int size() {
		return messageQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return messageQueue.isEmpty();
	}
	

}
