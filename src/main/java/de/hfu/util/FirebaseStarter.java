package de.hfu.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.DAO.User;

public class FirebaseStarter {
	
	
	public FirebaseStarter() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream("C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void checkRegister(final User user, ValueEventListener registerEvent){
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/");
		ref.addListenerForSingleValueEvent(registerEvent);
	}
	
	public void register(User user){
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUsername());
		ref.setValue(user);
	}

	public static void main(String[] args) throws Exception {
		FirebaseStarter firebaseStarter = new FirebaseStarter();
		User user = new User();
		user.setFirstname("max");
		user.setLastname("mustermann");
		user.setUsername("unique");
		user.setPassword("1234");
		firebaseStarter.register(user);
		Thread.sleep(1000);
	}

}
