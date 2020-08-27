package info.atarim.multiqueuing.exceptions;

public class QueueAlreadyExistsException extends MultiQueuingException {
	private static final long serialVersionUID = 6232771714589746748L;

	/**
	 * @param message
	 */
	public QueueAlreadyExistsException(String name) {
		super("Queue with name: " + name + " - already exists.");
	}
}
