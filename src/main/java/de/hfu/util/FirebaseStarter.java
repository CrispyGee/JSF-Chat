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
			serviceAccount = new FileInputStream("src/main/resources/JSFChat-3a2ee200916b.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
			//
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveUser(User user){
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").push();
		ref.setValue(user);
	}
	
	public void listen(){
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("top");
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				System.out.println(user);
				System.out.println(user.getFirstname());
				System.out.println(user.getLastname());
			}

			@Override
			public void onCancelled(DatabaseError er) {
				System.out.println(er);
			}
		});

	}
	
	public void setObjectToPath(Object object, String path){
	}

	public static void main(String[] args) throws Exception {
		FirebaseStarter firebaseStarter = new FirebaseStarter();
		User user = new User();
		user.setFirstname("soph");
		user.setLastname("lebof");
		firebaseStarter.saveUser(user);
		firebaseStarter.saveUser(user);
		Thread.sleep(1000);
	}

}
