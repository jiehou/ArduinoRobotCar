package com.filderbaer.arduinocarconsole;

public class Device {
	private String name = "";
	private String address = "";
	private String signal = "";

	public Device(String name, String address, Short signal) {
		this.name = name;
		this.address = address;
		this.signal = Short.toString(signal);
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getSignal() {
		return signal;
	}
}
