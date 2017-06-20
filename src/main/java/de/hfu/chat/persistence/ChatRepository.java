package de.hfu.chat.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.chat.model.Chat;
import de.hfu.chat.model.Message;

@ManagedBean(name = "chatRepository")
@ApplicationScoped
public class ChatRepository {
	
	public ChatRepository(){
		//default
	}
	
	public Chat createChat(String user1, String user2) {
		String chatId = UUID.randomUUID().toString();
		DatabaseReference user1Ref = FirebaseDatabase.getInstance().getReference("users/" + user1 + "/chats");
		DatabaseReference user2Ref = FirebaseDatabase.getInstance().getReference("users/" + user2 + "/chats");
		user1Ref.push().setValue(chatId);
		user2Ref.push().setValue(chatId);
		DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/chat");
		Chat chat = new Chat();
		chat.setCreator(user1);
		chat.setId(chatId);
		ArrayList<String> participants = new ArrayList<String>();
		participants.add(user1);
		participants.add(user2);
		chat.setParticipants(participants);
		chat.setTimestamp(System.currentTimeMillis());
		chatRef.setValue(chat);
		return chat;
	}

	public Chat loadChat(String id) {
		final Chat chat = new Chat();
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + id + "/chat");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Chat foundChat = snapshot.getValue(Chat.class);
				chat.copyChat(foundChat);
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
		chat.setMessages(loadMessages(id));
		return chat;
	}

	public List<Chat> loadChatList(String user) {
		// this list only contains the latest message of each chat
		List<String> chatIds = loadChatIds(user);
		final List<Chat> chats = new ArrayList<>();
		final int chatsProcessed[] = { 0 };
		for (final String id : chatIds) {
			final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + id + "/chat");
			ref.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					final Chat foundChat = snapshot.getValue(Chat.class);
					chats.add(foundChat);
					FirebaseDatabase.getInstance().getReference("chats/" + id + "/messages").limitToLast(1)
							.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(DataSnapshot snapshot) {
									if (snapshot.exists()) {
										System.out.println("snapshot LUL" + snapshot);
										Iterable<DataSnapshot> children = snapshot.getChildren();
										// there is only 1 message, but we still
										// have to iterate due to the structure
										for (DataSnapshot child : children) {
											Message lastMessage = child.getValue(Message.class);
											System.out.println(lastMessage);
											List<Message> messages = new ArrayList<Message>();
											messages.add(lastMessage);
											foundChat.setMessages(messages);
										}
									}
									chatsProcessed[0]++;
								}

								@Override
								public void onCancelled(DatabaseError err) {
									System.out.println(err);
								}
							});
				}

				@Override
				public void onCancelled(DatabaseError err) {
					System.out.println(err);
				}
			});
		}
		// wait for all requests to be done
		while (chatsProcessed[0] < chatIds.size()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (chats != null && !chats.isEmpty()) {
			sortChats(chats);
		}
		return chats;
	}

	
	//Helper Methods

	private List<String> loadChatIds(String user) {
		final List<String> chatIds = new ArrayList<>();
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user + "/chats");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
					String id = msgSnapshot.getValue(String.class);
					chatIds.add(id);
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
		return chatIds;
	}

	private void sortChats(final List<Chat> chats) {
		chats.sort(new Comparator<Chat>() {
			@Override
			public int compare(Chat o1, Chat o2) {
				if (o1.getMessages() == null && o2.getMessages() != null) {
					return 1;
				} else if (o2.getMessages() == null && o1.getMessages() != null) {
					return -1;
				} else if (o1.getMessages() == null && o2.getMessages() == null) {
					return o1.getTimestamp() < o2.getTimestamp() ? 1 : -1;
				} else {
					return o1.getMessages().get(0).getTimestamp() < o2.getMessages().get(0).getTimestamp() ? 1 : -1;
				}
			}
		});
	}

	private List<Message> loadMessages(String id) {
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats/" + id + "/messages");
		final List<Message> messages = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
					Message message = msgSnapshot.getValue(Message.class);
					messages.add(message);
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
