package info.atarim.multiqueuing.broker;

/**
 * @author vladimir
 *
 * @param <T>
 */
public interface MessageConsumer<T> {
	
	/**
	 * accept
	 * @param message
	 */
	void accept(Message<T> message);
}
