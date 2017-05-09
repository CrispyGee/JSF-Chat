package de.hfu.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.model.User;

public class FirebaseStarter {

	public FirebaseStarter() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream(
					"C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
					//"C://Users/IMTT/git/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public boolean login(String username, final String password) {
		final Semaphore semaphore = new Semaphore(0);
		final boolean success[] = { false };

		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + username);
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				try {
					User user = snap.getValue(User.class);
					success[0] = (user.getPassword().equals(password));
				} catch(Exception e){
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}

			@Override
			public void onCancelled(DatabaseError err) {

			}
		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return success[0];
	}

	public boolean register(final User user) {
		final Semaphore semaphore = new Semaphore(0);
		final boolean success[] = { false };
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot userSnapshot) {
				if (userSnapshot.hasChild(user.getUsername())) {
					System.out.println("username not available");
				} else {
					final DatabaseReference ref = FirebaseDatabase.getInstance()
							.getReference("users/" + user.getUsername());
					ref.setValue(user);
					System.out.println("succesfully registered");
					success[0] = true;
				}
				semaphore.release();
			}

			@Override
			public void onCancelled(DatabaseError err) {
				System.out.println(err);
			}
		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return success[0];
	}
	
	

}
