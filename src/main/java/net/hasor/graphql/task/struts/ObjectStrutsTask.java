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
package net.hasor.graphql.task.struts;
import net.hasor.graphql.task.QueryTask;
import net.hasor.graphql.task.TaskContext;
import net.hasor.graphql.task.source.SourceQueryTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectStrutsTask extends StrutsQueryTask {
    private List<String>           fieldList = new ArrayList<String>();
    private Map<String, QueryTask> dataMap   = new HashMap<String, QueryTask>();
    //
    public ObjectStrutsTask(TaskContext taskContext) {
        super(taskContext);
    }
    //
    public void addField(String name, SourceQueryTask dataSource) {
        FieldStrutsTask fieldStrutsTask = new FieldStrutsTask(this.getTaskContext(), name, dataSource);
        //
        this.fieldList.add(name);
        this.dataMap.put(name, fieldStrutsTask);
        super.addSubTask(fieldStrutsTask);
    }
    //
    @Override
    protected Object doTask(TaskContext taskContext) throws Throwable {
        return null;
    }
}