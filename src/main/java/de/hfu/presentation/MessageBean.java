package de.hfu.presentation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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
	private String otherUsername;

	public String getOtherUsername() {
		return otherUsername;
	}

	public void setOtherUsername(String otherUsername) {
		this.otherUsername = otherUsername;
	}

	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing MessageBean with init");
	}

	/**
	 * initializes User object using request parameter on page load
	 */
	public void initChatroom() {
		User currentUser = ((User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user"));
		if (currentUser != null) {
			this.user = currentUser;
		}
		this.username = this.user.getUsername();
		Chat currentChat = (Chat) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("chat");
		if (currentChat != null) {
			this.chat = currentChat;
			this.chat = FirebaseStarter.getInstance().loadChat(this.chat.getId());
		} else {
			//this.chat = new Chat(); //TODO: HIER MUSS N CHAT ERSTELLT WERDEn
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
		Message message = new Message(this.user.getUsername(), this.messageContent);
		this.chat.getMessages().add(message);
		FirebaseStarter.getInstance().sendMessage(this.chat.getId(),this.messageContent, this.user.getUsername());
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