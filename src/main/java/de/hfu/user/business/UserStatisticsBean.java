package de.hfu.user.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import de.hfu.services.DateFormatBean;
import de.hfu.user.model.User;
import de.hfu.user.persistence.UserRepository;

@ManagedBean
@ApplicationScoped
public class UserStatisticsBean {

	private List<User> users;

	@ManagedProperty(value = "#{userRepository}")
	private UserRepository userRepository;

	@ManagedProperty(value = "#{dateFormatBean}")
	private DateFormatBean dateFormatBean;

	private BarChartModel barModel;

	public void initUsers() {
		System.out.println("initing userStatisticsBean");
		this.users = userRepository.loadUserList();
		System.out.println(this.users);
		createBarModel();
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries usersPerMonth = new ChartSeries();
		usersPerMonth.setLabel("User pro Monat");
		TreeMap<String, Integer> monthUsers = new TreeMap<>();
		for (User user : users) {
			String formattedDate = dateFormatBean.formatSemi(user.getTimestamp());
			System.out.println(user);
			System.out.println(formattedDate);
			Integer current = monthUsers.get(formattedDate);
			if (current == null) {
				monthUsers.put(formattedDate, 1);
			} else {
				current++;
				monthUsers.put(formattedDate, current);
			}
		}
		for (String key : monthUsers.keySet()) {
			usersPerMonth.set(key, monthUsers.get(key));
		}
		model.addSeries(usersPerMonth);
		return model;
	}

	private void createBarModel() {
		barModel = initBarModel();

		barModel.setTitle("Registrierte User pro Monat");
		barModel.setLegendPosition("ne");

		Axis xAxis = barModel.getAxis(AxisType.X);
		xAxis.setLabel("Monat");

		Axis yAxis = barModel.getAxis(AxisType.Y);
		yAxis.setLabel("Anzahl");
		// yAxis.setMin(0);
		// yAxis.setMax(10);
	}

	public List<User> getUsers() {
		return users;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public DateFormatBean getDateFormatBean() {
		return dateFormatBean;
	}

	public void setDateFormatBean(DateFormatBean dateFormatBean) {
		this.dateFormatBean = dateFormatBean;
	}

}
