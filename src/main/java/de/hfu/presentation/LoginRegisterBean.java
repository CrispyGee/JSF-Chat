package de.hfu.presentation;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.hfu.model.User;
import de.hfu.services.FirebaseRepository;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class LoginRegisterBean implements Serializable {

	// input
	private String registerName;
	private String registerPassword;
	private String loginName;
	private String loginPassword;
	private String lastname;
	private String firstname;

	// output
	private String registerFail;
	private String registerSuccess;
	private String loginText;

	// utility
	private FirebaseRepository firebaseRepository;

	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing LoginRegisterBean with init");
		this.firebaseRepository = FirebaseRepository.getInstance();
	}

	public void initLogin() {
		this.registerFail = null;
		this.registerSuccess = null;
	}

	public String register() throws Exception {
		final User user = new User(firstname, lastname, registerName, registerPassword);
		if (firebaseRepository.register(user)) {
			this.setRegisterSuccess("Erfolgreich registriert!");
			this.registerFail = null;
		} else {
			this.setRegisterFail("Registrierung fehlgeschlagen: Accountname schon vergeben");
			this.registerSuccess = null;
		}
		return null;
	}

	public String login() {
		User user = firebaseRepository.login(loginName, loginPassword);
		if (user.getUsername() != null) {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", user);
			return "/chatoverview.xhtml?faces-redirect=true";
		} else {
			loginText = "Login fehlgeschlagen: Ungültige Anmeldedaten";
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

	public String getRegisterFail() {
		return registerFail;
	}

	public void setRegisterFail(String registerFail) {
		this.registerFail = registerFail;
	}

	public String getRegisterSuccess() {
		return registerSuccess;
	}

	public void setRegisterSuccess(String registerSuccess) {
		this.registerSuccess = registerSuccess;
	}

}
