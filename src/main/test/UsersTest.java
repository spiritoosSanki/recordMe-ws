package main.test;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.User;

public class UsersTest {

	private static RecordMe recMe;

	public static void main(String[] args) throws RecMeException {
		recMe = new RecordMe("http://localhost:9090/recordMe-ws");
		
		String sid = recMe.Users.login("sankar.grimm@gmail.com", "recme123");
		
		get(sid);
	}
	
	private static void get(String sid) throws RecMeException {
		User user = recMe.Users.get(sid, null);
		System.out.println(user);
	}

}
