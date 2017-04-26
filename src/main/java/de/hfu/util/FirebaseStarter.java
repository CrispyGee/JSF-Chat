package de.hfu.util;

import java.io.FileInputStream;

//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseCredentials;

public class FirebaseStarter {
	
//	<script src="https://www.gstatic.com/firebasejs/3.7.8/firebase.js"></script>
//	<script>
//	  // Initialize Firebase
//	  var config = {
//	    apiKey: "AIzaSyD05KgbHwWb8_P415UFzsytFE8NruPZ7PY",
//	    authDomain: "jsfchat.firebaseapp.com",
//	    databaseURL: "https://jsfchat.firebaseio.com",
//	    projectId: "jsfchat",
//	    storageBucket: "jsfchat.appspot.com",
//	    messagingSenderId: "560069890081"
//	  };
//	  firebase.initializeApp(config);
//	</script>
	
	public static void main(String[] args) throws Exception {
		FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");

//		FirebaseOptions options = new FirebaseOptions.Builder()
//		  .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
//		  .setDatabaseUrl("https://jsfchat.firebaseio.com/")
//		  .build();
//
//		FirebaseApp.initializeApp(options);
	}

}
