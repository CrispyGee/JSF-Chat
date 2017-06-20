package de.hfu.services;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class Utils {

	public static String logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.invalidate();
		
		return "/index.xhtml?faces-redirect=true";
	}
	
}