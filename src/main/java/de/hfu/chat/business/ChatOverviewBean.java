package de.hfu.chat.business;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.hfu.chat.model.Chat;
import de.hfu.chat.persistence.ChatRepository;
import de.hfu.services.SessionBean;
import de.hfu.user.model.User;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ChatOverviewBean implements Serializable {

	private User user;
	private List<Chat> chats;

	@ManagedProperty(value = "#{chatRepository}")
	private ChatRepository chatRepository;
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;
	

	public void initChats() {
		System.out.println("initializing ChatOverviewBean with init");
		this.user = sessionBean.getSessionUser();
		this.chats = this.chatRepository.loadChatList(this.user.getUsername());
	}

	public String displayMessage(Chat chat) {
		if (chat.getMessages() != null) {
			return chat.getMessages().get(0).getContent();
		} else
			return "";
	}

	public String displayTimestamp(Chat chat) {
		if (chat.getMessages() != null) {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm dd:MM:yyyy");
			return df.format(chat.getMessages().get(0).getTimestamp());
		} else
			return "";
	}

	public String otherUser(Chat chat) {
		for (String participant : chat.getParticipants()) {
			if (!participant.equals(user.getUsername())) {
				return participant;
			}
		}
		return "";
	}

	public String redirectToUserView() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", user);
		return "/userOverview.xhtml?faces-redirect=true";
	}

	public String redirectToChatOverview() {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", user);
		return "/chatOverview.xhtml?faces-redirect=true";
	}

	public String showChat(Chat chat) {
		System.out.println("______________________________-");
		System.out.println(this.user);
		System.out.println(chat);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", this.user);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("chat", chat);
		return "/chatRoom.xhtml?faces-redirect=true";
	}

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
