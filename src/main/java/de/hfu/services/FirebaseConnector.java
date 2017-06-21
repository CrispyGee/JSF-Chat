package de.hfu.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

public class FirebaseConnector {
	
	private static boolean CONNECTED = false;

	public static void connect() {
		if (!CONNECTED){
			FileInputStream serviceAccount;
			try {
				serviceAccount = new FileInputStream(
//						 "C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
											"C://Users/IMTT/git/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
						.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
				FirebaseApp.initializeApp(options);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
