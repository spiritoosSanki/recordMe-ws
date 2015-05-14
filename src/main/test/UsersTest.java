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
		
//		createAdmin(sid);
		
//		deleteAdmin(sid);
	}
	
	private static void deleteAdmin(String sid) throws RecMeException {
		recMe.Users.remove(sid, "an_admin1");
	}

	private static void get(String sid) throws RecMeException {
		User user = recMe.Users.get(sid, null);
		System.out.println(user);
	}

	private static void createAdmin(String sid) throws RecMeException {
		User user = new User();
		user.id = "an_admin1";
		user.email = "sankargrim.m@gmail.com";
		user.firstName = "sankar";
		user.lastName = "admin";
		user.password = "12345678";
		user.type = User.TYPE_ADMIN;
		recMe.Users.create(sid, user);
	}
}
