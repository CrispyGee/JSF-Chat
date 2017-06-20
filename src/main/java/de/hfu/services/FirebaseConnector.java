package de.hfu.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

public class FirebaseConnector {

	public static void connect() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream(
					 "C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			//			"C://Users/IMTT/git/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	


//	public static void main(String[] args) throws Exception {
//		FirebaseConnector fb = new FirebaseConnector();
		// fb.register(new User("b", "b", "b", "b"));
		// fb.register(new User("c", "c", "c", "c"));
		// fb.register(new User("d", "d", "d", "d"));
		// String chatId1 = fb.createChat("a", "b");
		// String chatId2 = fb.createChat("a", "c");
		// fb.createChat("a", "d");
		// fb.sendMessage(chatId1, "Hallo a", "b");
		// fb.sendMessage(chatId1, "Wie geht es dir?", "b");
		// Thread.sleep(2000);
		// fb.sendMessage(chatId2, "Hi hier ist c", "c");
		// Thread.sleep(1000);
		// List<Chat> chats_a = fb.loadChatOverview("a");
		// for (Chat chat : chats_a) {
		// System.out.println(chat);
		// fb.sendMessage(chat.getId(), "LUL", chat.getParticipants().get(1));
		// }
		// Thread.sleep(1000);
		// List<Chat> chats_a_2 = fb.loadChatOverview("a");
		// for (Chat chat : chats_a_2) {
		// System.out.println(chat);
		// }
//		List<String> filter = new ArrayList<>();
//		System.exit(0);
//	}

}
