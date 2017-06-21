package de.hfu.chat.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import de.hfu.chat.model.Message;
import de.hfu.chat.persistence.MessageRepository;
import de.hfu.services.DateFormatBean;

@ManagedBean
@ApplicationScoped
public class ChatStatisticsBean {

	private List<Message> messages;

	@ManagedProperty(value = "#{messageRepository}")
	private MessageRepository messageRepository;

	@ManagedProperty(value = "#{dateFormatBean}")
	private DateFormatBean dateFormatBean;

	private LineChartModel lineModel;

	public void initChats() {
		System.out.println("initing chatStatisticsBean");
		this.messages = messageRepository.loadAllMessages();
		System.out.println(this.messages);
		createLineModel();
	}

	private void createLineModel() {
		initLineModel();
		// initTestModel();
		lineModel.setTitle("Nachrichten diesen Monat");
		lineModel.setLegendPosition("ne");

		Axis xAxis = lineModel.getAxis(AxisType.X);
		xAxis.setLabel("Datum");
		xAxis.setMax(31);
		xAxis.setMin(0);
		xAxis.setTickCount(31);

		Axis yAxis = lineModel.getAxis(AxisType.Y);
		yAxis.setLabel("Anzahl");
	}

	private void initLineModel() {
		lineModel = new LineChartModel();
		LineChartSeries messagesPerMonth = new LineChartSeries();
		messagesPerMonth.setLabel("Nachrichten pro Tag");
		
		TreeMap<Integer, Integer> monthMessages = new TreeMap<>();
		for (Message message : messages) {
			java.util.Date date = new Date(message.getTimestamp());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			cal.setTime(new Date());
			int currentMonth = cal.get(Calendar.MONTH) + 1;
			if (month == currentMonth) {
				System.out.println(message);
				System.out.println(day);
				Integer current = monthMessages.get(day);
				if (current == null) {
					monthMessages.put(day, 1);
				} else {
					current++;
					monthMessages.put(day, current);
				}
			}
		}
		for (Integer key : monthMessages.keySet()) {
			System.out.println("key" + key);
			messagesPerMonth.set(key, monthMessages.get(key));
		}
		//for testing purposes
		messagesPerMonth.set(10, 10);
		messagesPerMonth.set(5, 5);
		lineModel.addSeries(messagesPerMonth);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public MessageRepository getMessageRepository() {
		return messageRepository;
	}

	public void setMessageRepository(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public DateFormatBean getDateFormatBean() {
		return dateFormatBean;
	}

	public void setDateFormatBean(DateFormatBean dateFormatBean) {
		this.dateFormatBean = dateFormatBean;
	}

	public LineChartModel getLineModel() {
		return lineModel;
	}

	public void setLineModel(LineChartModel lineModel) {
		this.lineModel = lineModel;
	}

}
