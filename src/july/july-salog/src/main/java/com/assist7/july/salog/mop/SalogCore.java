package com.assist7.july.salog.mop;

import java.lang.reflect.Method;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assist7.july.salog.annotation.Salog;
import com.assist7.july.salog.config.SalogDispatch;
import com.assist7.july.salog.config.SalogSetting;
import com.assist7.july.salog.config.etype.SalogUseType;
import com.assist7.july.salog.entity.SalogHdAo;
import com.assist7.july.salog.exception.JulySalogException;
import com.assist7.july.utils.bdata.StringUtils;
import com.assist7.july.utils.validate.ValidateUtils;

/**
 * salog日志打印核心处理器
 * 
 * @author Qiaoxin.Hong
 *
 */
public class SalogCore {
	
	/**
	 * salog配置参数
	 */
	protected SalogSetting setting = new SalogSetting();
	
	/**
	 * salog功能配置调度器
	 */
	protected SalogDispatch dispatch= new SalogDispatch();

	
	
	/**
	 * 由自定义AOP转入此处理方法，进行日志打印的相关处理
	 * @param point
	 * @return
	 */
	public Object handle(ProceedingJoinPoint point) throws Throwable{
		//是否是由接口方法抛出的异常
		boolean proceedException = false;
		//方法的处理结果
		Object result = null;
		
		try {
			//解析配置参数
			SalogHdAo hdAo = turnSalogHdAo();
			//解析方法对应的类Salog注解的相关配置
			Class<?> entityClass = processEntity(point, hdAo);
			//处理解析日志方法的Salog注解的相关配置
			processMethod(point, hdAo, entityClass);
			
			//日志打印器
			Logger logger = LoggerFactory.getLogger(entityClass);
			
			//打印入参相关
			writeLogIn(logger, hdAo, point.getArgs());
			
			long beginTime = System.currentTimeMillis();
			try {
				result = point.proceed();
			} catch (Throwable e) {
				proceedException = true;
				throw e;
			}
			
			//打印出参相关
			writeLogOut(logger, hdAo, beginTime, result);
			
			return result;
		} catch (Exception e) {
			//如果是由接口方法抛出的异常，在此不做会进行接口方法功能改动的操作
			if (proceedException)
				throw e;
			throw new JulySalogException("日志自动打印操作异常！", e);
		}
	}
	
	/**
	 * 打印入参相关
	 * @param logger
	 * @param hdAo
	 * @param args
	 */
	private void writeLogIn(Logger logger, SalogHdAo hdAo, Object[] args) {
		//打印方法开始
		if (hdAo.isIn()) {
			StringBuilder show = new StringBuilder();
			show.append(hdAo.getPrefix()).append(hdAo.getSegm()).append("开始");
			//拼接入参日志
			if (hdAo.isInArgs() && args != null && args.length != 0) {
				//排除要排除的参数对象，如有设
				args = excludeInArgsEntity(args);
				show.append(hdAo.getSegm()).append("入参：").append(dispatch.turnData(args));
			}
			show.append(hdAo.getSuffix());
			logger.info(show.toString());
		}
	}
	
	/**
	 * 打印出参相关
	 * @param logger
	 * @param hdAo
	 * @param beginTime
	 * @param result
	 */
	private void writeLogOut(Logger logger, SalogHdAo hdAo, long beginTime, Object result) {
		//打印方法结束
		if (hdAo.isOut()) {
			StringBuilder show = new StringBuilder();
			show.append(hdAo.getPrefix()).append(hdAo.getSegm()).append("结束");
			//拼接耗时
			if (hdAo.isTime()) {
				long endTime = System.currentTimeMillis();
				String consumeTimeStr = turnConsumeTime(beginTime, endTime);
				show.append(hdAo.getSegm()).append("耗时：").append(consumeTimeStr);
			}
			//拼接出参日志
			if (hdAo.isOurResult() && !Void.TYPE.equals(result)) {
				show.append(hdAo.getSegm()).append("出参：").append(dispatch.turnData(result));
			}
			show.append(hdAo.getSuffix());
			logger.info(show.toString());
		}
	}
	
	/**
	 * 解析配置参数为实体数据
	 * @return
	 */
	private SalogHdAo turnSalogHdAo() {
		SalogHdAo hdAo = new SalogHdAo();
		hdAo.setIn(SalogUseType.YES.equals(getSetting().getIn()));
		hdAo.setOut(SalogUseType.YES.equals(getSetting().getOut()));
		hdAo.setInArgs(SalogUseType.YES.equals(getSetting().getInArgs()));
		hdAo.setOurResult(SalogUseType.YES.equals(getSetting().getOutResult()));
		hdAo.setTime(SalogUseType.YES.equals(getSetting().getTime()));
		hdAo.setSegm(getSetting().getSegm());
		hdAo.setSuffix(getSetting().getSuffix());
		return hdAo;
	}
	
	/**
	 * 解析封装Salog注解中配置
	 * @param hdAo
	 * @param salog
	 */
	private void packSalogConfig(SalogHdAo hdAo, Salog salog) {
		if (!SalogUseType.EMPTY.equals(salog.in()))
			hdAo.setIn(SalogUseType.YES.equals(salog.in()));
		if (!SalogUseType.EMPTY.equals(salog.out()))
			hdAo.setOut(SalogUseType.YES.equals(salog.out()));
		if (!SalogUseType.EMPTY.equals(salog.inArgs()))
			hdAo.setInArgs(SalogUseType.YES.equals(salog.inArgs()));
		if (!SalogUseType.EMPTY.equals(salog.outResult()))
			hdAo.setOurResult(SalogUseType.YES.equals(salog.outResult()));
		if (!SalogUseType.EMPTY.equals(salog.time()))
			hdAo.setTime(SalogUseType.YES.equals(salog.time()));
		
		String segmItem = StringUtils.isNotBlank(hdAo.getPrefix()) 
				&& StringUtils.isNotBlank(salog.value()) ? hdAo.getSegm() : "";
		hdAo.setPrefix(hdAo.getPrefix() + segmItem + salog.value());
	}
	
	/**
	 * 处理解析日志方法对应的类的Salog注解的相关配置
	 * @param point
	 * @param hdAo
	 * @return
	 */
	private Class<?> processEntity(ProceedingJoinPoint point, SalogHdAo hdAo) {
		Class<?> entityClass = point.getTarget().getClass();
		if (entityClass.isAnnotationPresent(Salog.class)) {
			Salog salog = entityClass.getAnnotation(Salog.class);
			//解析封装实体上Salog注解的相关配置
			packSalogConfig(hdAo, salog);
		}
		return entityClass;
	}
	
	/**
	 * 处理解析日志方法的Salog注解的相关配置
	 * @param point
	 * @param hdAo
	 * @param entityClass
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Method processMethod(ProceedingJoinPoint point, SalogHdAo hdAo, Class<?> entityClass) throws NoSuchMethodException, SecurityException {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		//如果是接口的实现类，则取实现类的method
		if (!method.getClass().equals(entityClass))
			method = entityClass.getMethod(method.getName(), method.getParameterTypes());
		
		if (method.isAnnotationPresent(Salog.class)) {
			Salog salog = method.getAnnotation(Salog.class);
			//解析封装方法上Salog注解的相关配置
			packSalogConfig(hdAo, salog);
		}
		return method;
	}
	
	/**
	 * 在入参打印过程中，排除要排除的参数对象，如有设
	 * @param args
	 */
	private Object[] excludeInArgsEntity(Object[] args) {
		//如果有设置排除的参数对象，则进行排除再打印
		Set<Class<?>> excludeEntity = dispatch.excludeInArgsEntities();
		if (ValidateUtils.isNotEmpty(excludeEntity)) {
			Object[] itemArgs = new Object[args.length];
			label:
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg == null)
					continue;
				for (Class<?> clazz : excludeEntity) {
					if (clazz.isAssignableFrom(arg.getClass())) {
						itemArgs[i] = dispatch.excludeInArgsEntityHandle(arg);
						continue label;
					}
				}
				itemArgs[i] = arg;
			}
			args = itemArgs;
		}
		return args;
	}
	
	/**
	 * 转换耗时  extract
	 * @param beginTime 方法开始时间
	 * @param endTime 方法结束时间
	 * @return
	 */
	private String turnConsumeTime(long beginTime, long endTime) {
		long consumeTime = endTime - beginTime;
		long[] timeArr = new long[]{-1, -1, -1, -1};
		timeArr[3] = consumeTime % 1000;
		consumeTime = consumeTime / 1000;
		if (consumeTime != 0) {
			timeArr[2] = consumeTime % 60;
			consumeTime = consumeTime / 60;
		}
		if (consumeTime != 0) {
			timeArr[1] = consumeTime % 60;
			consumeTime = consumeTime / 60;
		}
		if (consumeTime != 0) {
			timeArr[0] = consumeTime % 60;
			consumeTime = consumeTime / 60;
		}
		
		return dispatch.turnConsumeTime(timeArr);
	}
	
	
	
	
	
	public SalogSetting getSetting() {
		return setting;
	}
	
	public void setSetting(SalogSetting setting) {
		this.setting = setting;
	}
	
	public void setDispatch(SalogDispatch dispatch) {
		this.dispatch = dispatch;
	}
	
	public SalogDispatch getDispatch() {
		return dispatch;
	}
}
