package de.hfu.presentation;

import java.io.Serializable;
import java.util.ArrayList;
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
public class UserBean implements Serializable {

	private List<User> users;
	private User user;

	public void initUsers() {
		User currentUser = (User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user");
		if (currentUser != null) {
			this.user = currentUser;
		}
		List<String> filter = new ArrayList<>();
		filter.add(this.user.getUsername());
		filter.addAll(getChatPartners(user.getUsername()));
		this.users = FirebaseStarter.getInstance().loadUserList(filter);

	}

	private List<String> getChatPartners(String username) {
		List<Chat> chats = FirebaseStarter.getInstance().loadChatList(username);
		List<String> chatPartners = new ArrayList<>();
		for (Chat chat : chats) {
			chatPartners.add(getOtherUser(chat.getParticipants()));
		}
		return chatPartners;
	}
	
	private String getOtherUser(List<String> participants){
		for (String participant : participants) {
			if (!participant.equals(user.getUsername())) {
				return participant;
			}
		}
		return "";
	}

	public String startChat(User user) throws Exception {
		FirebaseStarter.getInstance().createChat(this.user.getUsername(), user.getUsername());
		// TODO fix this bs
		Thread.sleep(1000);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", this.user);
		return "/chat.xhtml?faces-redirect=true";
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
