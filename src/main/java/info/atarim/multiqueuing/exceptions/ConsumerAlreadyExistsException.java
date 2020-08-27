package info.atarim.multiqueuing.exceptions;

public class ConsumerAlreadyExistsException extends MultiQueuingException {
	private static final long serialVersionUID = 6232771714589746748L;

	/**
	 * @param message
	 */
	public ConsumerAlreadyExistsException(String name) {
		super("Consumer for Queue with name: " + name + " - already exists.");
	}
}
