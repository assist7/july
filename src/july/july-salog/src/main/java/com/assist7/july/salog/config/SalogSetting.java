package com.assist7.july.salog.config;

import com.assist7.july.salog.config.etype.SalogUseType;

/**
 * salog配置参数容器
 * 
 * @author Qiaoxin.Hong
 *
 */
public class SalogSetting {
	
	/**
	 * 是否打印方法开始的日志，默认打印
	 */
	protected SalogUseType in = SalogUseType.YES;
	
	/**
	 * 是否打印方法结束的日志，默认打印
	 */
	protected SalogUseType out = SalogUseType.YES;
	
	/**
	 * 是否打印入参的日志，默认打印
	 */
	protected SalogUseType inArgs = SalogUseType.YES;
	
	/**
	 * 是否打印出参的日志，默认打印
	 */
	protected SalogUseType outResult = SalogUseType.YES;
	
	/**
	 * 是否打印耗时，默认打印
	 */
	protected SalogUseType time = SalogUseType.YES;
	
	/**
	 * 分割符，默认" - "
	 */
	protected String segm = " - ";
	
	/**
	 * 日志后缀，默认"！"
	 */
	protected String suffix = "！";
	
	
	public String getSegm() {
		return segm;
	}

	public void setSegm(String segm) {
		this.segm = segm;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return suffix;
	}

	public SalogUseType getIn() {
		return in;
	}

	public void setIn(SalogUseType in) {
		this.in = in;
	}

	public SalogUseType getOut() {
		return out;
	}

	public void setOut(SalogUseType out) {
		this.out = out;
	}

	public SalogUseType getInArgs() {
		return inArgs;
	}

	public void setInArgs(SalogUseType inArgs) {
		this.inArgs = inArgs;
	}

	public SalogUseType getOutResult() {
		return outResult;
	}

	public void setOutResult(SalogUseType outResult) {
		this.outResult = outResult;
	}
	
	public void setTime(SalogUseType time) {
		this.time = time;
	}
	
	public SalogUseType getTime() {
		return time;
	}
}
