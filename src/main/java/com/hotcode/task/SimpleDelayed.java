package com.hotcode.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 简单的Delayed,可以附加对象attach
 * 
 * @author qingquanwu
 *
 * @param <E>
 */
public class SimpleDelayed<E> implements Delayed {

	/**
	 * 时间戳标准值
	 */
	private static final long nanoOrigin = System.nanoTime() ;

	/**
	 * 需要延迟多久的纳秒值
	 */
	private long delayNanoseconds;

	/**
	 * 
	 */
	private E attach ;
	
	/**
	 * 
	 * @return
	 */
	private final long now() {
		return System.nanoTime() - nanoOrigin;
	}
	
	
	/**
	 * 
	 */
	public SimpleDelayed() {}
	
	/**
	 * 
	 * @param attach
	 * @param time
	 * @param unit
	 */
	public SimpleDelayed(E attach, long time, TimeUnit unit) {
		this.attach = attach ;
		setDelayTime(time, unit) ;
	}
	
	/**
	 * 
	 * @return
	 */
	public E getAttach() {
		return attach;
	}

	
	/**
	 * 
	 * @param attach
	 * @return
	 */
	public SimpleDelayed<E> setAttach(E attach) {
		this.attach = attach;
		return this ;
	}

	
	/**
	 * 
	 * @return
	 */
	public long getDelayNanoseconds() {
		return delayNanoseconds;
	}
	
	
	/**
	 * 
	 * @param time 要推迟的时间(TimeUnit.NANOSECONDS)
	 * @return
	 */
	public SimpleDelayed<E> setDelayTime(long time){
		this.delayNanoseconds = now() + time;
		return this ;
	}

	
	/**
	 * 
	 * @param time
	 *            要推迟的时间
	 * @param unit
	 *            TimeUnit单位
	 * @return
	 */
	public SimpleDelayed<E> setDelayTime(long time, TimeUnit unit) {
		return setDelayTime(TimeUnit.NANOSECONDS.convert(time, unit));
	}
	
	
	/**
	 * 返回要延迟多久
	 */
	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delayNanoseconds - now(), TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this) return 0;
		SimpleDelayed<?> oth = (SimpleDelayed<?>) other;
		return (delayNanoseconds < oth.delayNanoseconds ? -1 : (delayNanoseconds == oth.delayNanoseconds ? 0 : 1));
	}
	
}
