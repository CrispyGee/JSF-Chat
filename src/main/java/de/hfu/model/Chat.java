package de.hfu.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IMTT on 12.04.2017.
 */
@SuppressWarnings("serial")
public class Chat implements Serializable{

	private String name;
	private String id;
	private long timestamp;
	private List<String> participants;
	private String creator;
	private List<Message> messages;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void copyChat(Chat chat) {
		this.creator = chat.creator;
		this.id = chat.id;
		this.name = chat.name;
		this.participants = chat.participants;
		this.timestamp = chat.timestamp;
		this.messages = chat.messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "Chat [name=" + name + ", id=" + id + ", timestamp=" + timestamp + ", participants=" + participants
				+ ", creator=" + creator + ", messages=" + messages + "]";
	}
	
}
