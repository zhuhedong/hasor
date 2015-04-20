/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package test.net.hasor.rsf.rpc.provider;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Monitor implements RsfFilter {
    private AtomicLong sendCount = new AtomicLong(0);
    private long       startTime = System.currentTimeMillis() / 1000;
    //
    public void printInfo() {
        long checkTime = System.currentTimeMillis() / 1000;
        if (checkTime - startTime == 0) {
            return;
        }
        long qps = (sendCount.get() / (checkTime - startTime));
        System.out.println("count:" + sendCount + "\tQPS:" + qps);
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        sendCount.getAndIncrement();
        if (sendCount.get() % 10000 == 0) {
            printInfo();
        }
        chain.doFilter(request, response);
    }
}