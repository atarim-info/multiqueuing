package info.atarim.multiqueuing.exceptions;

public abstract class MultiQueuingException extends Exception {

	/**
	 * @param message
	 */
	public MultiQueuingException(String message) {
		super(message);
	}
}
