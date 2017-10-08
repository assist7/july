package com.assist7.july.salog.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.assist7.july.salog.mop.SalogCore;

/**
 * 日志自动打印的父拦截器，默认拦截实现Salog注解的方法，以aop环绕通知来进行日志打印处理
 * 
 * @see com.assist7.july.salog.annotation.Salog
 * 
 * @author Qiaoxin.Hong
 *
 */
@Aspect
public class SalogInterceptor {
	/**
	 * salog日志打印核心处理器
	 */
	protected SalogCore salogCore = new SalogCore();
	
	/**
	 * 切点，默认拦截实现Salog注解的方法
	 * @see com.assist7.july.salog.annotation.Salog
	 */
	@Pointcut("@annotation(com.assist7.july.salog.annotation.Salog)" )
	protected void salogMethod() {}
	
	/**
	 * aop环绕通知
	 * @param point
	 * @return
	 * @throws Throwable 
	 */
	@Around("salogMethod()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		return salogCore.handle(point);
	}
	
	
	
	public SalogCore getSalogCore() {
		return salogCore;
	}
	
	public void setSalogCore(SalogCore salogCore) {
		this.salogCore = salogCore;
	}
}
