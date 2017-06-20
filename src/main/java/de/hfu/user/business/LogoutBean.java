package de.hfu.user.business;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import de.hfu.user.persistence.UserRepository;

@ManagedBean
@SessionScoped
public class LogoutBean {

	@ManagedProperty(value = "#{userRepository}")
	private UserRepository userRepository;

	public String logout(String username) {
		if (username != "") {
			userRepository.logout(username);
		}

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//session.invalidate();
		session.setAttribute("user", "");
		System.out.println("______________________________");
		System.out.println(session);
		System.out.println("_________________________________");

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

}
