package de.hfu.user.business;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.NamedEvent;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;

import de.hfu.services.FirebaseConnector;
import de.hfu.user.model.User;
import de.hfu.user.persistence.UserRepository;

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
	@ManagedProperty(value="#{userRepository}")
	private UserRepository userRepository;
	
	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing LoginRegisterBean with init");
		FirebaseConnector.connect();
	}

	public void initLogin() {
		this.registerFail = null;
		this.registerSuccess = null;
	}

	public String register() throws Exception {
		final User user = new User(firstname, lastname, registerName, registerPassword);
		if (userRepository.register(user)) {
			this.setRegisterSuccess("Erfolgreich registriert!");
			this.registerFail = null;
		} else {
			this.setRegisterFail("Registrierung fehlgeschlagen: Accountname schon vergeben");
			this.registerSuccess = null;
		}
		return null;
	}

	public String login() {
		User user = userRepository.login(loginName, loginPassword);
		if (user.getUsername() != null) {
			//FacesContext.getCurrentInstance().getExternalContext().getFlash().put("user", user);
						
			// store session
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			
			session.setAttribute("user", user);
			
			return "/chatOverview.xhtml?faces-redirect=true";
		} else {
			loginText = "Login fehlgeschlagen: Ungültige Anmeldedaten";
			return null;
		}
	}
	
	@Size(min=3, max=10, message="Min 3 and max 10 characters")
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

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	

}
