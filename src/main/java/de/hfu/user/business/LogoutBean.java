package de.hfu.user.business;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import de.hfu.services.SessionBean;
import de.hfu.user.persistence.UserRepository;

@ManagedBean
@SessionScoped
public class LogoutBean {

	@ManagedProperty(value = "#{userRepository}")
	private UserRepository userRepository;
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	public String logout(String username) {
		if (username != "") {
			userRepository.logout(username);
		}
		sessionBean.killSession();
		return "/index.xhtml?faces-redirect=true";
	}

	public String backToLogin() {
		return "/index.xhtml?faces-redirect=true";
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
