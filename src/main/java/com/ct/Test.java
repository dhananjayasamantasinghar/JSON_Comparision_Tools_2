package com.ct;

import com.ct.model.POA_Employee;
import com.ct.model.POD_Employee_1;

public class Test {

	public static void main(String[] args) {
		Test.run();
	}

	private static void run() {
		POA_Employee poaObject1 = CommpoUtils.getDataObject(POA_Employee.class, "./static/poa.json");
		POD_Employee_1 podObject = CommpoUtils.getDataObject(POD_Employee_1.class, "./static/pod.json");
		POA_Employee poaObject2 = CommpoUtils.convertToPoaEmployee(podObject);

		System.out.println(poaObject1);
		System.out.println(poaObject2);
	}

}
