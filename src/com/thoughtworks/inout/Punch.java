package com.thoughtworks.inout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Punch implements Parcelable {
	private PunchType type;
	private Date date;
	
	public Punch(PunchType type, Date date) {
		this.type = type;
		this.date = date;
	}
	
	public Punch(Parcel in) {
		readFromParcel(in);
	}

	public PunchType getType() {
		return type;
	}

	public void setType(PunchType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String toString() {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		return this.type.toString() + " - " + df.format(this.date);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.type.ordinal());
		dest.writeLong(this.date.getTime());
	}
	
	private void readFromParcel(Parcel in) {		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		int punchOrdinal = in.readInt();
		long timestamp = in.readLong();
		this.date = new Date(timestamp);
		this.type = PunchType.values()[punchOrdinal];
	}
}
