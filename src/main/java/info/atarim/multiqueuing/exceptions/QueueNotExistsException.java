package info.atarim.multiqueuing.exceptions;

public class QueueNotExistsException extends MultiQueuingException {
	private static final long serialVersionUID = -7841846715828280259L;

	/**
	 * @param message
	 */
	public QueueNotExistsException(String name) {
		super("Queue with name: " + name + " - doesn't exists.");
	}
}
