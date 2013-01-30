package com.thoughtworks.inout;

import java.util.Date;

import com.thoughtworks.inout.clock.Clock;
import com.thoughtworks.inout.db.TimeCardDAO;

public class TimeCard {
	private TimeCardDAO dao;

	public TimeCard(TimeCardDAO dao) {
		this.dao = dao;
	}
	
	public Date punch(PunchType type) {
		Date now = Clock.now();
		dao.insertPunch(new Punch(type, now));
		return now;
	}

}
