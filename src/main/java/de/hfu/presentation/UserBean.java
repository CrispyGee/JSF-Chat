package de.hfu.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.hfu.model.User;
import de.hfu.util.FirebaseStarter;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class UserBean implements Serializable {
	
	private List<User> users;
	private User user;
	
	public void initUsers(){
		this.user = (User) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user");
		List<String> filter = new ArrayList<>();
		filter.add(this.user.getUsername());
		this.users = FirebaseStarter.getInstance().loadUserList(filter);

	}
	
	public String startChat(User user) throws Exception {
		FirebaseStarter.getInstance().createChat(this.user.getUsername(), user.getUsername());
		//TODO fix this bs
		Thread.sleep(1000);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", this.user);
		return "/chatoverview.xhtml?faces-redirect=true";
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
