package de.hfu.presentation;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hfu.model.User;
import de.hfu.util.FirebaseStarter;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class LoginRegisterBean implements Serializable {

	//input
	private String registerName;
	private String registerPassword;
	private String loginName;
	private String loginPassword;
	private String lastname;
	private String firstname;
	
	//output
	private String registerText;
	private String loginText;

	//utility
	private FirebaseStarter firebaseStarter;



	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing LoginRegisterBean with init");
		this.firebaseStarter = new FirebaseStarter();
	}

	public String register() throws Exception {
		final User user = new User(firstname, lastname, registerName, registerPassword);
		if (firebaseStarter.register(user)) {
			this.registerText = "SUCCES";
		} else {
			this.registerText = "FAIL";
		}
		return null;
	}
	
	public String login() {
		if (firebaseStarter.login(loginName, loginPassword)) {
			return "/chat.xhtml?faces-redirect=true&username=" + loginName;
		} else {
			loginText = "Wrong Login Credentials";
			return null;
		}
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String username) {
		this.registerName = username;
	}

	public String getRegisterPassword() {
		return registerPassword;
	}

	public void setRegisterPassword(String password) {
		this.registerPassword = password;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getRegisterText() {
		return registerText;
	}

	public void setRegisterText(String registerText) {
		this.registerText = registerText;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginText() {
		return loginText;
	}

	public void setLoginText(String loginText) {
		this.loginText = loginText;
	}

}
