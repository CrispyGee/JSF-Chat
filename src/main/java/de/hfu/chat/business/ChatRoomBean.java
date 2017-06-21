package de.hfu.chat.business;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import de.hfu.chat.model.Chat;
import de.hfu.chat.model.Message;
import de.hfu.chat.persistence.ChatRepository;
import de.hfu.chat.persistence.MessageRepository;
import de.hfu.services.SessionBean;
import de.hfu.user.model.User;

@ManagedBean
@SessionScoped
public class ChatRoomBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private User user;
	private String messageContent;
	private Chat chat;
	private Map<String, Boolean> messageReceive;
	private String otherUsername;

	@ManagedProperty(value = "#{messageRepository}")
	private MessageRepository messageRepository;

	@ManagedProperty(value = "#{chatRepository}")
	private ChatRepository chatRepository;
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;
	

	public void receiveMessages() {

		messageRepository.onReceiveMessage(this.chat.getId(), new ChildEventListener() {

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
				System.out.println("received " + message);
				chat.addMessage(message);
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
		System.out.println("initializing chatroom");
		if (messageReceive == null) {
			messageReceive = new HashMap<>();
		}
		this.messageContent = "";
		this.user = sessionBean.getSessionUser();
		this.username = this.user.getUsername();
		Chat currentChat = (Chat) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("chat");
		String previousChat = "";
		if (this.chat != null) {
			previousChat = this.chat.getId();
		}
		if (previousChat.equals(currentChat.getId())) {
			// do nothing is this case, since this chat is already loaded
		} else {
			this.chat = chatRepository.loadChat(currentChat.getId());
			if (messageReceive.get(currentChat.getId()) == null || !messageReceive.get(currentChat.getId())) {
				this.chat.setMessages(new ArrayList<Message>());
				receiveMessages();
				messageReceive.put(this.chat.getId(), true);
			}
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
		System.out.println("sending message");
		MessageRepository messageRepo = new MessageRepository();
		messageRepo.sendMessage(this.chat.getId(), this.messageContent, this.user.getUsername());
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

	public String redirectToChatOverview() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", user);
		return "/chatOverview.xhtml?faces-redirect=true";
	}

	private String getOtherUser(List<String> participants) {
		for (String participant : participants) {
			if (!participant.equals(user.getUsername())) {
				return participant;
			}
		}
		return "";
	}

	public MessageRepository getMessageRepository() {
		return messageRepository;
	}

	public void setMessageRepository(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public ChatRepository getChatRepository() {
		return chatRepository;
	}

	public void setChatRepository(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
	
	

}