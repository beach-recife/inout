package com.thoughtworks.inout.clock;

import java.util.Date;

import com.thoughtworks.inout.clock.Clock;

public class TimeMachine {
	
	public static Date freeze() {
		Clock.fixed_milis = new Date().getTime();
		return Clock.now(); 
	}
	
	public static Date backToPresent() {
		Clock.fixed_milis = null;
		return Clock.now();
	}	
}
