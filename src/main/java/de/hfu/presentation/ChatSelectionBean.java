package de.hfu.presentation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.hfu.model.Chat;
import de.hfu.model.User;
import de.hfu.util.FirebaseStarter;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ChatSelectionBean implements Serializable {
	private User user;
	private List<Chat> chats;

	public void initChats() {
		System.out.println("initializing ChatSelectionBean with init");
		User currentUser = (User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user");
		if (currentUser!=null){
			this.user=currentUser;
		}
		this.chats = FirebaseStarter.getInstance().loadChatList(this.user.getUsername());
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
		return "/users.xhtml?faces-redirect=true";
	}

	public String showChat(Chat chat) {
		System.out.println("______________________________-");
		System.out.println(this.user);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", this.user);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("chat", chat);
		return "/chat.xhtml?faces-redirect=true";
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
}
