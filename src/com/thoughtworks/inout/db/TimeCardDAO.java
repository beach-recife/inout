package com.thoughtworks.inout.db;

import java.util.Date;

import com.thoughtworks.inout.PunchType;

public interface TimeCardDAO {

	public abstract void insertPunch(Date punchDate, PunchType punchType);

}