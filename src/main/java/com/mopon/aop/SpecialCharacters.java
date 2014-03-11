package com.mopon.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component("specialCharacters")
public class SpecialCharacters {
	
	@Before("execution(* com.mopon.service.impl.*.*(..))") 
	public void beforeCharachters(JoinPoint joinPoint) {
		/*System.out.println("===================specialCharacters========================");
		Object[] params = joinPoint.getArgs();
		for(Object o : params) {
			
		}*/
	}
}
