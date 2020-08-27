package info.atarim.multiqueuing.data;

public class UserData<T> {
	String userId;
	T origin;
	
	/**
	 * @param userId
	 * @param origin
	 */
	public UserData(String userId, T origin) {
		this.userId = userId;
		this.origin = origin;
	}
}
