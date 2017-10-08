package com.assist7.july.salog.config;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;

/**
 * salog功能配置调度器
 * 
 * @author Qiaoxin.Hong
 *
 */
public class SalogDispatch {
	/**
	 * 转换数据为字符串，默认转为json
	 * @param data 数据，如入参列表或出参结果集
	 * @return
	 */
	public String turnData(Object data) {
		return JSONObject.toJSONString(data);
	}
	
	/**
	 * 转换耗时为日志字符串，默认如：3秒27毫秒、1小时7分2秒19毫秒
	 * @param timeArr 时间参数，下标：0：小时；1：分；2：秒；3：毫秒。值为-1时，则表示没有值
	 * @return
	 */
	public String turnConsumeTime(long[] timeArr) {
		StringBuilder sb = new StringBuilder();
		if (timeArr[0] != -1)
			sb.append(timeArr[0]).append("小时");
		if (timeArr[1] != -1)
			sb.append(timeArr[1]).append("分");
		if (timeArr[2] != -1)
			sb.append(timeArr[2]).append("秒");
		if (timeArr[3] != -1)
			sb.append(timeArr[3]).append("毫秒");
		
		return sb.toString();
	}
	
	/**
	 * 在入参打印过程中，会直接排除的参数对象集
	 * @return
	 */
	public Set<Class<?>> excludeInArgsEntities() {
		return null;
	}
	
	/**
	 * 在入参打印过程中，被排除的参数对象的最终打印方式
	 * @param arg
	 * @return
	 */
	public Object excludeInArgsEntityHandle(Object arg) {
		return "exclude";
	}
}
