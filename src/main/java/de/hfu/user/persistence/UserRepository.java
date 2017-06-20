package de.hfu.user.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hfu.user.model.OnlineState;
import de.hfu.user.model.User;

@ManagedBean(name = "userRepository")
@ApplicationScoped
public class UserRepository {
	
	public UserRepository(){
		//default
	}
	
	public User login(final String username, final String password) {
		final Semaphore semaphore = new Semaphore(0);
		final User userBack[] = { new User() };
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + username + "/user");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				try {
					User user = snap.getValue(User.class);
					//if user was found, mark him as online
					final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("users/" + username + "/user/onlineState");
					ref2.setValue(OnlineState.online);
					userBack[0] = user;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}

			@Override
			public void onCancelled(DatabaseError err) {

			}
		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return userBack[0];
	}
	
	/**
	 * Logs out user asynchronously - flags as offline
	 * 
	 * @param username
	 */
	public void logout(String username) {
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + username + "/user/onlineState");
		ref.setValue(OnlineState.offline);
	}

	public boolean register(final User user) {
		final Semaphore semaphore = new Semaphore(0);
		final boolean success[] = { false };
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot userSnapshot) {
				if (userSnapshot.hasChild(user.getUsername())) {
					System.out.println("username not available");
				} else {
					final DatabaseReference ref = FirebaseDatabase.getInstance()
							.getReference("users/" + user.getUsername() + "/user");
					ref.setValue(user);
					System.out.println("succesfully registered");
					success[0] = true;
				}
				semaphore.release();
			}

			@Override
			public void onCancelled(DatabaseError err) {
				System.out.println(err);
			}
		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return success[0];
	}

	public List<User> loadUserList(List<String> filterUsers) {
		final Semaphore semaphore = new Semaphore(0);
		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/");
		final List<User> users = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot innerSnap : snapshot.getChildren()) {
					DataSnapshot userSnap = innerSnap.child("user");
					User user = userSnap.getValue(User.class);
					user.setPassword(null);
					users.add(user);
				}
				semaphore.release();
			}

			@Override
			public void onCancelled(DatabaseError err) {
				System.out.println(err);
			}
		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		final List<User> usersFiltered = new ArrayList<>();
		for (User user : users) {
			if (!filterUsers.contains(user.getUsername())) {
				usersFiltered.add(user);
			}
		}
		return usersFiltered;
	}
	
	public List<User> loadUserList() {
		return this.loadUserList(new ArrayList<String>());
	}

}
