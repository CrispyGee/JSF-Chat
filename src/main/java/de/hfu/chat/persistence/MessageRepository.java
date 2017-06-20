package de.hfu.chat.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
	
	public List<Message> loadAllMessages() {
		final List<Message> messages = new ArrayList<>();
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot msgSnapshot1 : snapshot.getChildren()) {
					DataSnapshot msgSnapshot2 = msgSnapshot1.child("messages");
					for (DataSnapshot msgSnapshot3 : msgSnapshot2.getChildren()) {
						Message message = msgSnapshot3.getValue(Message.class);
						messages.add(message);
					}
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
		return messages;
	}

}
