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
	private List<Message> messages;
	private Chat chat;

	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing MessageBean with init");
		this.messages = new ArrayList<Message>();
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
			this.messages = chat.getMessages();
		} else {
			//this.chat = new Chat(); //TODO: HIER MUSS N CHAT ERSTELLT WERDEn
		}
		// TODO if no user redirect
	}

	/**
	 * Send a message to the chat on button action
	 *
	 * @param e
	 */
	public void send(ActionEvent e) {
		Message message = new Message(this.user.getUsername(), this.messageContent);
		this.messages.add(message);
		FirebaseStarter.getInstance().sendMessage(this.chat.getId(),this.messageContent, this.user.getUsername());
		this.messageContent = "";
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
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

	public String formatDate(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(timestamp);
	}

}