package de.hfu.presentation;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hfu.DAO.User;
import de.hfu.util.FirebaseStarter;

@ManagedBean
@SessionScoped
public class LoginRegisterBean {

	private String username;
	private String password;
	private String lastname;
	private String firstname;
	private String registerSuccess;
	private FirebaseStarter firebaseStarter;

	public String login() {
		// TODO check if valid creds
		// navigation
		return "/chat.xhtml?faces-redirect=true&username=" + username;
	}

	/**
	 * initializes Bean with necessary objects (nearly same as a constructor)
	 */
	@PostConstruct
	public void init() {
		System.out.println("initializing LoginRegisterBean with init");
		this.firebaseStarter = new FirebaseStarter();
		registerSuccess = "Registrieren";
	}

	public String register() throws Exception {
		final User user = new User(firstname, lastname, username, password);
		ValueEventListener registerListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot userSnapshot) {
				if (userSnapshot.hasChild(user.getUsername())) {
					System.out.println("danke dafür");
					registerSuccess = "Username not available";
				} else {
					firebaseStarter.register(user);
					registerSuccess = "Erfolgreich registriert";
				}

			}

			@Override
			public void onCancelled(DatabaseError arg0) {
			}
		};
		firebaseStarter.checkRegister(user, registerListener);
		//TODO make synchronous on request
		Thread.sleep(2500);
		return null;
	}

	public void reload() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		String viewId = context.getViewRoot().getViewId();
		ViewHandler handler = context.getApplication().getViewHandler();
		UIViewRoot root = handler.createView(context, viewId);
		root.setViewId(viewId);
		context.setViewRoot(root);
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

	public String getRegisterSuccess() {
		return registerSuccess;
	}

	public void setRegisterSuccess(String registerSuccess) {
		this.registerSuccess = registerSuccess;
	}

}
