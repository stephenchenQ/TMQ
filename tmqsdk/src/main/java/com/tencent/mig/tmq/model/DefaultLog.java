package com.tencent.mig.tmq.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DefaultLog<T, M> implements ILogger<T, M> {
	static int CAPACITY = 256;

	IFilter<T, M> filter;

	// 过滤前的实际消息序列，要有上限，避免一直不校验撑爆内存
	protected List<M> preFilterMsgQueue = new LinkedList<>();

	// 过滤后的实际消息序列，要有上限，避免一直不校验撑爆内存
	protected List<M> msgQueue = new ArrayList<>();

	// 通过校验的消息序列
	protected Queue<M> checkedQueue = new LinkedList<>();

	@Override
	public boolean append(M msg) {
		if (preFilterMsgQueue.size() < CAPACITY * 2)
			preFilterMsgQueue.add(msg);

		if (filter.validate(msg)) {
			if (msgQueue.size() < CAPACITY) {
				msgQueue.add(msg);
			}
		}
		else
		{
			return false;
		}

		return true;
	}

	@Override
	public void appendCheckedMsg(M msg) {
		checkedQueue.add(msg);
	}

	@Override
	public void clear() {
		preFilterMsgQueue.clear();
		msgQueue.clear();
		checkedQueue.clear();
	}

	/**
	 * 默认的日志过程输出,
	 * 分三段输出，分别是过滤前的完整消息序列，通过过滤的消息序列，通过校验的消息序列
	 */
	@Override
	public String[] getHistory()
	{
		String[] result = new String[3];
		StringBuilder preFilterMsgs = new StringBuilder();
		for (M s : preFilterMsgQueue)
		{
			preFilterMsgs.append(s);
			preFilterMsgs.append(System.getProperty("line.separator"));
		}
		result[0] = preFilterMsgs.toString();
		
		StringBuilder msgs = new StringBuilder();
		for (M s : msgQueue)
		{
			msgs.append(s);
			preFilterMsgs.append(System.getProperty("line.separator"));
		}
		result[1] = msgs.toString();
		
		StringBuilder checkedMsgs = new StringBuilder();
		for (M s : checkedQueue)
		{
			checkedMsgs.append(s);
			preFilterMsgs.append(System.getProperty("line.separator"));
		}
		result[2] = checkedMsgs.toString();
		
		return result;
	}

	@Override
	public void setFilter(IFilter<T, M> filter) {
		this.filter = filter;
	}

	@Override
	public List<M> getPreFilterQueue() {
		return preFilterMsgQueue;
	}

	@Override
	public List<M> getAfterFilterQueue() {
		return msgQueue;
	}

	@Override
	public Queue<M> getCheckedQueue() {
		return checkedQueue;
	}
}
