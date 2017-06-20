package de.hfu.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.model.Chat;
import de.hfu.model.Message;
import de.hfu.model.User;
//Service Klassen entstehen
//bilden eigene Schicht
//dritte Schicht mit repository klasse als singleton

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
					"C://Users/Christian/workspace/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
			// "C://Users/IMTT/git/JSF-Chat/src/main/resources/JSFChat-3a2ee200916b.json");
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

	public boolean sendMessage(String chatId, String content, String sender) {
		Message message = new Message();
		message.setContent(content);
		message.setTimestamp(System.currentTimeMillis());
		message.setSender(sender);
		DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages");
		chatRef.push().setValue(message);
		return true;
	}

	public void onReceiveMessage(String chatId, ChildEventListener childEventListener) {
		DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages");
		chatRef.addChildEventListener(childEventListener);
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
		fb.register(new User("Nicock", "Nicock", "Nicock", "Nicock"));
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
		List<User> users = fb.loadUserList(filter);
		System.out.println(users);
		System.exit(0);
	}

}
