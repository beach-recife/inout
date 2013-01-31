package com.thoughtworks.inout;

public enum PunchType {
	IN("In"),
	OUT("Out");
	
	private String value;
	
	PunchType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static PunchType getTypeOf(String val) {
		PunchType pType = null;
		if (IN.getValue() == val) {
			pType = IN;
		} else if (OUT.getValue() == val) {
			pType = OUT;
		}
		return pType;
	}
}
