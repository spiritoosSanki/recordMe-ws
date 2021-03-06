package main.test;

import java.util.Date;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.User;

public class UsersTest {

	private static RecordMe recMe;

	public static void main(String[] args) throws RecMeException {
		recMe = new RecordMe("http://localhost:9090/recordMe-ws");

//		String sid = recMe.Users.login("sankar.grimm@gmail.com", "recme123"); //root
		String sid = recMe.Users.login("sankargrim.m@gmail.com", "12345678"); //admin
		
//		get(sid);
		
//		createAdmin(sid);
		
		createUser(sid);
		
//		deleteAdmin(sid);
		
		System.out.println("Done");
	}
	
	private static void deleteAdmin(String sid) throws RecMeException {
		recMe.Users.remove(sid, "an-admin1");
	}

	private static void get(String sid) throws RecMeException {
		User user = recMe.Users.get(sid, null);
		System.out.println(user);
	}

	private static void createAdmin(String sid) throws RecMeException {
		User user = new User();
		user.id = "an-admin1";
		user.email = "sankargrim.m@gmail.com";
		user.firstName = "sankar";
		user.lastName = "admin";
		user.password = "12345678";
		user.type = User.TYPE_ADMIN;
		recMe.Users.create(sid, user);
	}

	private static void createUser(String sid) throws RecMeException {
		User user = new User();
		user.id = "a-user1";
		user.email = "sankargri.mm@gmail.com";
		user.firstName = "sankar";
		user.lastName = "GRIMM";
		user.password = "12345678";
		user.type = User.TYPE_USER;
		Date birth = new Date();
		birth.setDate(3);
		birth.setMonth(1);
		birth.setYear(1986);
		user.birth = RecordMe.getIso8601UTCDateString(birth);
		recMe.Users.create(sid, user);
	}
}
