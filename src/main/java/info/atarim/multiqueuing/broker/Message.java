package info.atarim.multiqueuing.broker;

import info.atarim.multiqueuing.data.UserData;

public class Message<T> {
	UserData sender;
	T messagePayload;

	/**
	 * Constructor
	 * @param messagePayload
	 */
	public Message(UserData sender, T messagePayload) {
		this.sender = sender;
		this.messagePayload = messagePayload;
	}

	public UserData getSender() {
		return sender;
	}

	public T getMessagePayload() {
		return messagePayload;
	}
	
}
