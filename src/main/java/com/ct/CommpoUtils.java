package com.ct;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import com.ct.model.POA_Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommpoUtils {

	private static Map<String, Object> objectFactory = new HashMap<>();

	public static void doSimpleMapping(Object podEmployee, Object poa_Employee, Field field, String mappingValue) {
		try {
			Method[] declaredMethods = poa_Employee.getClass().getDeclaredMethods();
			for (Method method : declaredMethods) {
				if (method.getName().equalsIgnoreCase("set" + mappingValue)) {
					field.setAccessible(true);
					method.invoke(poa_Employee, ReflectionUtils.getField(field, podEmployee));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doComplexMapping(Object podEmployee, Object poa_Employee, Field field, String mappingValue)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		String[] values = mappingValue.split("/");

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			if (i == 0) {
				setFirstComplexType(podEmployee, poa_Employee, field, values, value);
			} else if (i > 0 && i != values.length - 1) {
				setComplexTypeExceptFirst(values, i, value);
			} else {
				setFinalData(podEmployee, field, values, i, value);
			}
		}

	}

	private static void setFinalData(Object podEmployee, Field field, String[] values, int i, String value)
			throws IllegalAccessException, InvocationTargetException {
		Object obj1 = objectFactory.get(values[i - 1]);
		Method[] declaredMethods = obj1.getClass().getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.getName().equalsIgnoreCase("set" + value)) {
				field.setAccessible(true);
				method.invoke(obj1, ReflectionUtils.getField(field, podEmployee));
			}
		}
	}

	private static void setComplexTypeExceptFirst(String[] values, int i, String value)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException {
		Object obj2 = objectFactory.get(values[i - 1]);

		Field[] declaredFields1 = obj2.getClass().getDeclaredFields();

		for (Field field2 : declaredFields1) {
			Method[] declaredMethods = obj2.getClass().getDeclaredMethods();
			String[] split = field2.toGenericString().split(" ");
			String className = split[1];
			String newValue = value;
			if (className.contains(value)) {
				newValue = field2.getName();
			}
			for (Method method : declaredMethods) {
				if (method.getName().equalsIgnoreCase("set" + newValue)) {
					Object obj = Class.forName(className).newInstance();
					method.invoke(obj2, obj);
					objectFactory.put(values[i - 1], obj2);
					objectFactory.put(value, obj);
				}
			}
		}
	}

	private static void setFirstComplexType(Object podEmployee, Object poa_Employee, Field field, String[] values,
			String value)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException {
		Field[] declaredFields = poa_Employee.getClass().getDeclaredFields();
		for (Field field2 : declaredFields) {
			String[] split = field2.toGenericString().split(" ");
			String className = split[1];
			String newValue = value;
			if (className.contains(value)) {
				newValue = field2.getName();
			}
			if (field2.getName().equalsIgnoreCase(newValue)) {
				Object obj = Class.forName(className).newInstance();
				for (Method method : poa_Employee.getClass().getDeclaredMethods()) {
					if (method.getName().equalsIgnoreCase("set" + newValue)) {
						method.invoke(poa_Employee, obj);
						objectFactory.put(value, obj);
						break;
					}
				}
			}
		}
	}

	public static POA_Employee convertToPoaEmployee(Object podEmployee) {
		try {
			return getPodEmployee(podEmployee, POA_Employee.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static <T> T getPodEmployee(Object podEmployee, Class<T> clazz) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Field[] declaredFields = podEmployee.getClass().getDeclaredFields();
		T poa_Employee = (T) clazz.newInstance();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(POAMapping.class)) {
				String mappingValue = field.getAnnotation(POAMapping.class).value();
				if (mappingValue.contains("/")) {
					CommpoUtils.doComplexMapping(podEmployee, poa_Employee, field, mappingValue);
				} else {
					CommpoUtils.doSimpleMapping(podEmployee, poa_Employee, field, mappingValue);
				}
			}
		}
		return poa_Employee;
	}

	public static <T> T getDataObject(Class<T> clazz, String path) {
		try {
			String content = AppReader.getContentFromFile(path);
			return new ObjectMapper().readValue(content, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
