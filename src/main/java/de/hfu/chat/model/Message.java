package de.hfu.chat.model;

/**
 * Created by IMTT on 12.04.2017.
 */
public class Message {

	private String content;
	private String sender;
	private MessageState messageState;
	private long timestamp;

	public Message() {
		this.timestamp = System.currentTimeMillis();
		this.messageState = MessageState.sent;
	}

	public Message(String sender, String content) {
		this.timestamp = System.currentTimeMillis();
		this.messageState = MessageState.sent;
		this.sender = sender;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public MessageState getMessageState() {
		return messageState;
	}

	public void setMessageState(MessageState messageState) {
		this.messageState = messageState;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Message [content=" + content + ", sender=" + sender + ", messageState=" + messageState + ", timestamp="
				+ timestamp + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((messageState == null) ? 0 : messageState.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (messageState != other.messageState)
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

}
