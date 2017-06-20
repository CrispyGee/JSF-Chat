package de.hfu.user.business;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import de.hfu.chat.model.Chat;
import de.hfu.chat.persistence.ChatRepository;
import de.hfu.user.model.User;
import de.hfu.user.persistence.UserRepository;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class UserOverviewBean implements Serializable {

	private List<User> users;
	private User user;

	@ManagedProperty(value = "#{userRepository}")
	private UserRepository userRepository;

	@ManagedProperty(value = "#{chatRepository}")
	private ChatRepository chatRepository;

	public void initUsers() {
		this.userRepository = new UserRepository();
		this.chatRepository = new ChatRepository();
		 HttpSession session = (HttpSession)
		 FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 User currentUser = (User) session.getAttribute("user");

//		User currentUser = (User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user");

		if (currentUser != null) {
			this.user = currentUser;
		} else {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml?faces-redirect=true");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String> filter = new ArrayList<>();
		filter.add(this.user.getUsername());
		filter.addAll(getChatPartners(user.getUsername()));
		this.users = userRepository.loadUserList(filter);

	}

	private List<String> getChatPartners(String username) {
		List<Chat> chats = chatRepository.loadChatList(username);
		List<String> chatPartners = new ArrayList<>();
		for (Chat chat : chats) {
			chatPartners.add(getOtherUser(chat.getParticipants()));
		}
		return chatPartners;
	}

	private String getOtherUser(List<String> participants) {
		for (String participant : participants) {
			if (!participant.equals(user.getUsername())) {
				return participant;
			}
		}
		return "";
	}

	public String startChat(User user) throws Exception {
		Chat chat = chatRepository.createChat(this.user.getUsername(), user.getUsername());
		// TODO fix this bs
		Thread.sleep(1000);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", this.user);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("chat", chat);
		return "/chatRoom.xhtml?faces-redirect=true";
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

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ChatRepository getChatRepository() {
		return chatRepository;
	}

	public void setChatRepository(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

}
