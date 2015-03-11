package com.hotcode.logger;

import com.hotcode.logger.Logger.Level;

/**
 * 一行日志就是一个LogItem
 */
final class LogItem {
	/**
	 * 日志产生时间
	 */
	private long time; // 该log发生的时间
	/**
	 * 日志级别
	 */
	private Level level; // 该log的level

	/**
	 * 日志正文内容
	 */
	private String str;

	/**
	 * 异常信息对象
	 */
	private ThrowableInfo throwInfo = null;

	public LogItem(Level level, String str, Throwable th) {
		this.time = System.currentTimeMillis();
		this.level = level;
		this.str = str;
		if (th != null) {
			this.throwInfo = new ThrowableInfo(th);
		}
	}

	public long getTime() {
		return this.time;
	}

	public String getStr() {
		return this.str;
	}

	public ThrowableInfo getThrowInfo() {
		return this.throwInfo;
	}

	String toStringNoEndReturn() {
		StringBuilder strBuf = new StringBuilder();
		strBuf.append(Util.getDateAllInfo(this.time)).append("\t");
		strBuf.append(this.level.name()).append("\t");
		strBuf.append(this.str);
		if (this.throwInfo != null) {
			strBuf.append(this.throwInfo.getThrowableStr());
		}
		return strBuf.toString();
	}

	/**
	 * 获取每个log的描述
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Util.getDateAllInfo(this.time)).append("\t");
		builder.append(this.level.name()).append("\t");
		builder.append(this.str).append("\n");
		if (this.throwInfo != null) {
			builder.append(this.throwInfo.getThrowableStr());
		}
		return builder.toString();
	}
}