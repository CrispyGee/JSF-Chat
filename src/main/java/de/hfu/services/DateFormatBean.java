package de.hfu.services;

import java.text.SimpleDateFormat;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ApplicationScoped
@ManagedBean(name="dateFormatBean")
public class DateFormatBean {

	public String formatSmall(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(timestamp);
	}

	public String formatFull(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
		return df.format(timestamp);
	}
	
	public String formatSemi(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("MM.yyyy");
		return df.format(timestamp);
	}
	
	public String formatSemiWithDay(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		return df.format(timestamp);
	}

}
