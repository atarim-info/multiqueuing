package info.atarim.multiqueuing.broker;

import java.util.UUID;

public interface MessageQueue<T> {

	String getName();

	UUID getQueueId();

	boolean offer(Message<T> e);

	Message<T> poll();

	int size();

	boolean isEmpty();

}