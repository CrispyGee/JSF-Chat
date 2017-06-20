package de.hfu.chat.persistence;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hfu.chat.model.Message;


@ManagedBean(name = "messageRepository")
@ApplicationScoped
public class MessageRepository {
	
	public MessageRepository(){
		//default
	}
	
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
