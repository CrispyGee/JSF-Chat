package de.hfu.presentation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class LoginBean {
	
	private String username;
	
	public String login() {
		//TODO check if valid creds
		//navigation
	    return "/chat.xhtml?faces-redirect=true&username=" + username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	

}
