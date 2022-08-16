package com.kh.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LogAspect {

	@Pointcut("execution(* com.kh.spring.todo..*(..))")
	public void pointcut() {}
	
	@Around("pointcut()")
	public Object logAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature(); // 메소드 시그니쳐
		String typeName = signature.getDeclaringTypeName(); // 클래스명
		String methodName = signature.getName(); // 메소드명
		
		// Before
		log.debug("{}.{} 호출전!", typeName, methodName);
		
		Object obj = joinPoint.proceed();

		// After
		log.debug("{}.{} 호출후!", typeName, methodName);
		
		return obj;
	}
	
}
