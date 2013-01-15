package com.thoughtworks.inout.clock;

import java.util.Date;

public class Clock {
	
	static Long fixed_milis = null;

	public static Date now() {
		return fixed_milis == null ? new Date() : new Date(fixed_milis);
	}

}
