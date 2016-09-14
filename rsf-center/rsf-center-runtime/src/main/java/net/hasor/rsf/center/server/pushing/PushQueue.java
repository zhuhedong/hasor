/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.center.server.pushing;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.server.domain.RsfCenterSettings;
import net.hasor.rsf.center.server.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class PushQueue implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<PushEvent>         dataQueue;
    private ArrayList<Thread>                      threadPushQueue;
    private Map<RsfCenterEventEnum, PushProcessor> processorMapping;
    @Inject
    private RsfContext                             rsfContext;
    @Inject
    private RsfCenterSettings                      rsfCenterCfg;
    //
    @Init
    public void init() {
        AppContext app = this.rsfContext.getAppContext();
        this.processorMapping = new HashMap<RsfCenterEventEnum, PushProcessor>();
        for (RsfCenterEventEnum eventType : RsfCenterEventEnum.values()) {
            PushProcessor processor = app.getInstance(eventType.getProcessorType());
            this.processorMapping.put(eventType, processor);
        }
        logger.info("pushQueue processor mapping ->{}", JsonUtils.toJsonString(this.processorMapping.keySet()));
        //
        this.dataQueue = new LinkedBlockingQueue<PushEvent>();
        this.threadPushQueue = new ArrayList<Thread>();
        for (int i = 1; i <= 3; i++) {
            Thread pushQueue = this.createPushThread(i);
            pushQueue.start();
            this.threadPushQueue.add(pushQueue);
        }
        logger.info("PushQueue Thread start.");
    }
    /**创建推送线程*/
    private Thread createPushThread(int index) {
        Thread threadPushQueue = new Thread(this);
        threadPushQueue.setDaemon(true);
        threadPushQueue.setName("Rsf-Center-PushQueue-" + index);
        return threadPushQueue;
    }
    public void run() {
        while (true) {
            try {
                PushEvent pushEvent = null;
                while ((pushEvent = this.dataQueue.take()) != null) {
                    doPush(pushEvent);
                }
            } catch (Throwable e) {
                logger.error("doPushQueue - " + e.getMessage(), e);
            }
        }
    }
    //
    // - 立刻执行消息推送,返回推送失败的地址列表。
    private List<InterAddress> doPush(PushEvent pushEvent) {
        PushProcessor pushProcessor = this.processorMapping.get(pushEvent.getPushEventType());
        if (pushProcessor != null) {
            logger.info("pushEvent {} -> {}", pushEvent.getPushEventType().name(), pushEvent);
            return pushProcessor.doProcessor(pushEvent);
        } else {
            logger.error("pushEvent pushProcessor is null. {} -> {}", pushEvent.getPushEventType().name(), pushEvent);
        }
        return pushEvent.getTarget();
    }
    // - 将消息推送交给推送线程,执行异步推送。
    public boolean doPushEvent(PushEvent eventData) {
        if (this.dataQueue.size() > this.rsfCenterCfg.getPushQueueMaxSize()) {
            try {
                Thread.sleep(this.rsfCenterCfg.getPushSleepTime());
            } catch (Exception e) {
                logger.error("pushQueue - " + e.getMessage(), e);
            }
            if (this.dataQueue.size() > this.rsfCenterCfg.getPushQueueMaxSize()) {
                return false;//资源还是紧张,返回 失败
            }
        }
        //
        this.dataQueue.offer(eventData);//资源不在紧张，加入到队列中。
        return true;
    }
}