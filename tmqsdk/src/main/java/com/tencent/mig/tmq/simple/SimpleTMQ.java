/*
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.mig.tmq.simple;

import com.tencent.mig.tmq.model.IExecuteController;
import com.tencent.mig.tmq.model.IExecuteControllers;
import com.tencent.mig.tmq.model.IExpectModes;
import com.tencent.mig.tmq.model.IFilters;
import com.tencent.mig.tmq.model.ITmq;

import java.io.OutputStream;

public class SimpleTMQ implements ITmq<String, SimpleTmqMsg>  {

	private IExecuteController<String, SimpleTmqMsg> controller;

	public SimpleTMQ()
	{
		// 默认的设置
		this.setExecuteController(ControllerEnum.SIMPLE)
				.switchFilter(FilterEnum.TYPE)
				.switchExpectMode(ModeEnum.STRICT);
	}
	
	public SimpleTMQ setExecuteController(IExecuteControllers<String, SimpleTmqMsg> controller)
	{
		this.controller = controller.getController();
		return this;
	}
	
	public SimpleTMQ switchFilter(IFilters<String, SimpleTmqMsg> filter) {
		controller.switchFilter(filter);
		return this;
	}

	public SimpleTMQ switchExpectMode(IExpectModes<String, SimpleTmqMsg> mode) {
		controller.switchExpectMode(mode);
		return this;
	}

	public boolean check(){
		return controller.check();
	}

	public boolean report(String tag, Object msg) {
		return controller.report(tag, msg);
	}

	/**
	 * 有效的消息序列初始化
	 * 
	 * @param tmqMsgList
	 *            有效的消息序列
	 */
	@Override
	public void iCareWhatMsg(SimpleTmqMsg... tmqMsgList) {
		controller.reset(tmqMsgList.length);
		for (SimpleTmqMsg msg : tmqMsgList)
		{
			controller.willCare(msg);
		}
	}

	/**
	 * 有效的消息过滤标识
	 * 
	 * @param msgTypes
	 *            有效的消息类型
	 */
	public void iCareWhatType(String... msgTypes) {
		for (String tag : msgTypes)
		{
			controller.willFilter(tag);
		}
	}

	public void clearCaredType() {
		controller.clearFilter();
	}

	public void reset() {
		controller.clear();
	}

	/**
	 * await的同时要进行结果检查
	 * @return
	 */
	public boolean await() {
		return await(10);
	}

	public boolean await(long timeout) {
		return controller.await(timeout);
	}
	
	public void setOutStream(OutputStream os)
	{
		controller.setOutInfo(os);
	}

	public void printHistory(String head, String foot) {
		if (null != head) {
			controller.print(head.concat(System.getProperty("line.separator")));
		}
		controller.print();
		if (null != foot) {
			controller.print(foot.concat(System.getProperty("line.separator")));
		}
	}

	public void printText(String head, String foot, String text) {
		if (null != head) {
			controller.print(head.concat(System.getProperty("line.separator")));
		}
		if (null != text) {
			controller.print(text.concat(System.getProperty("line.separator")));
		}
		if (null != foot) {
			controller.print(foot.concat(System.getProperty("line.separator")));
		}
	}
}
