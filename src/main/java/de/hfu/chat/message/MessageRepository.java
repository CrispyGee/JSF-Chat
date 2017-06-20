package de.hfu.chat.message;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageRepository {
	
	public boolean sendMessage(String chatId, String content, String sender) {
		Message message = new Message();
		message.setContent(content);
		message.setTimestamp(System.currentTimeMillis());
		message.setSender(sender);
		DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages");
		chatRef.push().setValue(message);
		return true;
	}
	
	public void onReceiveMessage(String chatId, ChildEventListener childEventListener){
		DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages");
		chatRef.addChildEventListener(childEventListener);
	}

}
