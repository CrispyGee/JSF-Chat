package de.hfu.presentation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import de.hfu.model.Chat;
import de.hfu.model.Message;
import de.hfu.model.User;
import de.hfu.util.FirebaseStarter;

@ManagedBean
@SessionScoped
public class MessageBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private User user;
	private String messageContent;
	private Chat chat;
	private boolean messageReceive = false;
	private String otherUsername;

	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing MessageBean with init");
	}

	public void receiveMessages() {
		FirebaseStarter.getInstance().onReceiveMessage(this.chat.getId(), new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildAdded(DataSnapshot msgSnapshot, String arg1) {
				Message message = msgSnapshot.getValue(Message.class);
				chat.addMessage(message);
				messageReceive = true;
			}

			@Override
			public void onCancelled(DatabaseError arg0) {
			}
		});
	}

	/**
	 * initializes User object using request parameter on page load
	 */
	public void initChatroom() throws Exception {
		User currentUser = ((User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user"));
		if (currentUser != null) {
			this.user = currentUser;
		}
		this.username = this.user.getUsername();
		Chat currentChat = (Chat) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("chat");
		if (currentChat != null) {
			this.chat = currentChat;
			this.chat.setMessages(new ArrayList<Message>());
		} else {
			// this.chat = new Chat(); //TODO: HIER MUSS N CHAT ERSTELLT WERDEn
		}
		if (!messageReceive) {
			Thread.sleep(1000);
			receiveMessages();
		}
		this.otherUsername = getOtherUser(this.chat.getParticipants());
		// TODO if no user redirect
	}

	/**
	 * Send a message to the chat on button action
	 *
	 * @param e
	 */
	public void send(ActionEvent e) {
		FirebaseStarter.getInstance().sendMessage(this.chat.getId(), this.messageContent, this.user.getUsername());
		this.messageContent = "";
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public String getOtherUsername() {
		return otherUsername;
	}

	public void setOtherUsername(String otherUsername) {
		this.otherUsername = otherUsername;
	}

	public String formatDate(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(timestamp);
	}

	private String getOtherUser(List<String> participants) {
		for (String participant : participants) {
			if (!participant.equals(user.getUsername())) {
				return participant;
			}
		}
		return "";
	}

}