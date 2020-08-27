/**
 * 
 */
package info.atarim.multiqueuing.broker;

import info.atarim.multiqueuing.concurrent.ThreadPool;

/**
 * @author vladimir
 *
 */
public class MessageBrokerFactory {
	
	/**
	 * @param threadCount
	 */
	private MessageBrokerFactory() {
	}
	
	
	/**
	 * createMessageBroker
	 * @return
	 */
	public static <T> MessageBroker<T> createMessageBroker(int threadCount) {
		MessageBroker<T> messageBrokerInstance = null; //get from bean factory 
		if (messageBrokerInstance == null) {
			ThreadPool threadPool = ThreadPool.createThreadPoll(threadCount);
			messageBrokerInstance = new MessageBrokerImpl<>(threadPool);
		}
		return messageBrokerInstance;
	}	

}
