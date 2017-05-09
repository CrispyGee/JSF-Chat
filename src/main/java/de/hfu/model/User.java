package de.hfu.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IMTT on 12.04.2017.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

	private String firstname;
	private String lastname;
	private String username;
	private String password;
	private OnlineState onlineState;
	private List<Chat> openChats;

	public User() {

	}

	public User(String firstname, String lastname, String username, String password) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public OnlineState getOnlineState() {
		return onlineState;
	}

	public void setOnlineState(OnlineState onlineState) {
		this.onlineState = onlineState;
	}

	public List<Chat> getOpenChats() {
		return openChats;
	}

	public void setOpenChats(List<Chat> openChats) {
		this.openChats = openChats;
	}

	@Override
	public String toString() {
		return "User [firstname=" + firstname + ", lastname=" + lastname + ", username=" + username + ", password="
				+ password + ", onlineState=" + onlineState + ", openChats=" + openChats + "]";
	}

}
