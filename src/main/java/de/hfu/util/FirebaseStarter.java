package de.hfu.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.model.Chat;
import de.hfu.model.Message;
import de.hfu.model.User;

public class FirebaseStarter {

	private static FirebaseStarter singleton;

	public static FirebaseStarter getInstance() {
		if (singleton == null) {
			FirebaseStarter.singleton = new FirebaseStarter();
		}
		return singleton;
	}

	private FirebaseStarter() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream(
					// "C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
					"C://Users/IMTT/git/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl("https://jsfchat.firebaseio.com/").build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public User login(String username, final String password) {
		final Semaphore semaphore = new Semaphore(0);
		final User userBack[] = { new User() };
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + username + "/user");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				try {
					User user = snap.getValue(User.class);
					userBack[0] = user;
				} catch (Exception e) {
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
		return userBack[0];
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
							.getReference("users/" + user.getUsername() + "/user");
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

	public List<User> loadUserList(List<String> filterUsers) {
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/");
		final List<User> users = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot innerSnap : snapshot.getChildren()) {
					DataSnapshot userSnap = innerSnap.child("user");
					User user = userSnap.getValue(User.class);
					user.setPassword(null);
					users.add(user);
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
		final List<User> usersFiltered = new ArrayList<>();
		for (User user : users) {
			if (!filterUsers.contains(user.getUsername())) {
				usersFiltered.add(user);
			}
		}
		return usersFiltered;
	}

	public String createChat(String user1, String user2) {
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
		return chatId;
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
							.addValueEventListener(new ValueEventListener() {
								@Override
								public void onDataChange(DataSnapshot snapshot) {
									if (snapshot.exists()) {
										System.out.println(snapshot);
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
									System.out.println("message processed");
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
		return chats;
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

	public static void main(String[] args) throws Exception {
		FirebaseStarter fb = new FirebaseStarter();
		// fb.register(new User("a", "a", "a", "a"));
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
		List<String> filter = new ArrayList<>();
		filter.add("a");
		filter.add("b");
		List<User> users = fb.loadUserList(filter);
		System.out.println(users);
		System.exit(0);
	}

}
