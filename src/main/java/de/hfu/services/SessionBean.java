package de.hfu.services;

import java.io.IOException;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import de.hfu.user.model.User;

@ApplicationScoped
@ManagedBean(name = "sessionBean")
public class SessionBean {

	public void establishSession(User user) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("user", user);
	}

	public User getSessionUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User currentUser = (User) session.getAttribute("user");
		// User currentUser = (User)
		// FacesContext.getCurrentInstance().getExternalContext().getFlash().get("user");
		if (currentUser == null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml?faces-redirect=true");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return currentUser;
	}

	public void killSession() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		session.invalidate();
		session.setAttribute("user", null);
	}

}
