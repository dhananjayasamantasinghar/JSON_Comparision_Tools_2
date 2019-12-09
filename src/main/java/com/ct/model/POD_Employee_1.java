package com.ct.model;

import com.ct.POAMapping;

public class POD_Employee_1 {

	@POAMapping("name")
	private String eName;
	
	@POAMapping("Address/addressLine1")
	private String eAddress;
	
	@POAMapping("mobile")
	private String eMobile;
	
	@POAMapping("salary")
	private double eSalary;
	
	public String geteName() {
		return eName;
	}
	public void seteName(String eName) {
		this.eName = eName;
	}
	public String geteAddress() {
		return eAddress;
	}
	public void seteAddress(String eAddress) {
		this.eAddress = eAddress;
	}
	public String geteMobile() {
		return eMobile;
	}
	public void seteMobile(String eMobile) {
		this.eMobile = eMobile;
	}
	public double geteSalary() {
		return eSalary;
	}
	public void seteSalary(double eSalary) {
		this.eSalary = eSalary;
	}
	@Override
	public String toString() {
		return "POD_Employee_1 [eName=" + eName + ", eAddress=" + eAddress + ", eMobile=" + eMobile + ", eSalary="
				+ eSalary + "]";
	}

	
	

}
