/**
 * 
 */
package com.alladeson.caurisit.config;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

/**
 * @author allad
 *
 */
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		// TODO Auto-generated method stub
		System.out.println("Exception Cause - " + ex.getMessage());
		System.out.println("Method name - " + method.getName());
		for (Object param : params) {
			System.out.println("Parameter value - " + param);
		}
	}

}
