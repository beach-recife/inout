package com.thoughtworks.inout.db;

import java.util.Date;

import com.thoughtworks.inout.Punch;
import com.thoughtworks.inout.exception.DataRetrieveException;

public interface TimeCardDAO {

	public abstract void insertPunch(Punch punch);
	public abstract Date[] getAllRegisterDates() throws DataRetrieveException;
	public abstract Punch getLastPunch() throws DataRetrieveException;
	public abstract Punch[] getAllPunchesFor(Date d) throws DataRetrieveException;

}