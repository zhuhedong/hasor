/*
 * Copyright 2008-2009 the original ������(zyc@hasor.net).
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
package org.more.webui.component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.BeanUtils;
import org.more.webui.DataException;
import org.more.webui.component.support.NoState;
import org.more.webui.component.values.AbstractValueHolder;
import org.more.webui.component.values.ExpressionValueHolder;
import org.more.webui.component.values.MethodExpression;
import org.more.webui.component.values.StaticValueHolder;
import org.more.webui.context.ViewContext;
import org.more.webui.event.Event;
import org.more.webui.event.EventListener;
import org.more.webui.lifestyle.phase.InitView_Phase;
/**
 * <b>�齨ģ��</b>����������ĸ�������ӵ����������йؼ�������
 * <br><b>������¼�</b>��OnLoadData
 * <br><b>��Ⱦ��</b>����
* @version : 2011-8-4
* @author ������ (zyc@byshell.org)
*/
public abstract class UIComponent {
    private String                           componentID   = null;
    private String                           componentPath = null;
    private UIComponent                      parent        = null;
    private List<UIComponent>                components    = new ArrayList<UIComponent>();
    private Map<Event, List<EventListener>>  listener      = new HashMap<Event, List<EventListener>>();
    private Map<String, AbstractValueHolder> propertys     = new HashMap<String, AbstractValueHolder>();
    private Map<String, Object>              atts          = new HashMap<String, Object>();
    /*-------------------------------------------------------------------------------get/set����*/
    /**���������ID*/
    public String getComponentID() {
        return componentID;
    }
    /**��������ID*/
    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }
    /**ͨ�����Ա�*/
    public static enum Propertys {
        /**�ͻ���������֮ǰ���еĵ��ã�����falseȡ������ajax����R��*/
        beforeScript,
        /**�ͻ��˽ű��ص�������R��*/
        afterScript,
        /**���ô���ص�������R��*/
        errorScript,
        /**Ajax�Ƿ�ʹ��ͬ��������R��*/
        async,
        /**��ʾ�Ƿ���Ⱦ��-��*/
        render,
        /**��ʾ�Ƿ���Ⱦ���齨��-��*/
        renderChildren,
        /**�������¼�OnLoadDataʱ���������¼������û�ͨ�������齨�ӷ����װ�����ݵ��ͻ��ˡ���R��*/
        onLoadDataEL,
        /**�����¼�ʱ��URL����Я���Ĳ�������RW��*/
        ajaxParam,
    };
    /**�������ͨ���÷�����ʼ�������*/
    protected void initUIComponent(ViewContext viewContext) {
        /*��������Ĭ��ֵ����ҳ������ֵ�����õ�ʱ���������õ�Ĭ��ֵ�ͻ�ʧЧ*/
        this.setPropertyMetaValue(Propertys.beforeScript.name(), "true");
        this.setPropertyMetaValue(Propertys.afterScript.name(), null);
        this.setPropertyMetaValue(Propertys.errorScript.name(), null);
        this.setPropertyMetaValue(Propertys.async.name(), true);//Ĭ��ʹ���첽�����¼�
        this.setPropertyMetaValue(Propertys.render.name(), true);
        this.setPropertyMetaValue(Propertys.renderChildren.name(), true);
        this.setPropertyMetaValue(Propertys.onLoadDataEL.name(), null);
        this.setPropertyMetaValue(Propertys.ajaxParam.name(), null);
        this.addEventListener(Event.getEvent("OnLoadData"), new Event_OnLoadData());
    };
    public String getBeforeScript() {
        return this.getProperty(Propertys.beforeScript.name()).valueTo(String.class);
    }
    @NoState
    public void setBeforeScript(String beforeScript) {
        this.getProperty(Propertys.beforeScript.name()).value(beforeScript);
    }
    public String getAfterScript() {
        return this.getProperty(Propertys.afterScript.name()).valueTo(String.class);
    }
    @NoState
    public void setAfterScript(String afterScript) {
        this.getProperty(Propertys.afterScript.name()).value(afterScript);
    }
    public String getErrorScript() {
        return this.getProperty(Propertys.errorScript.name()).valueTo(String.class);
    }
    @NoState
    public void setErrorScript(String errorScript) {
        this.getProperty(Propertys.errorScript.name()).value(errorScript);
    }
    public boolean isAsync() {
        return this.getProperty(Propertys.async.name()).valueTo(Boolean.TYPE);
    }
    @NoState
    public void setAsync(boolean async) {
        this.getProperty(Propertys.async.name()).value(async);
    }
    /**����һ��booleanֵ����ֵ�����Ƿ���Ⱦ�����*/
    @NoState
    public boolean isRender() {
        return this.getProperty(Propertys.render.name()).valueTo(Boolean.TYPE);
    };
    /**����һ��booleanֵ����ֵ�����Ƿ���Ⱦ�����*/
    @NoState
    public void setRender(boolean isRender) {
        this.getProperty(Propertys.render.name()).value(isRender);
    };
    /**����һ��booleanֵ����ֵ�����Ƿ���Ⱦ����������齨��*/
    @NoState
    public boolean isRenderChildren() {
        return this.getProperty(Propertys.renderChildren.name()).valueTo(Boolean.TYPE);
    }
    /**����һ��booleanֵ����ֵ�����Ƿ���Ⱦ����������齨��*/
    @NoState
    public void setRenderChildren(boolean isRenderChildren) {
        this.getProperty(Propertys.renderChildren.name()).value(isRenderChildren);
    }
    /**����ͼװ������ʱEL���ñ���ʽ��������ã�*/
    public String getOnLoadDataEL() {
        return this.getProperty(Propertys.onLoadDataEL.name()).valueTo(String.class);
    }
    /**����ͼװ������ʱEL���ñ���ʽ��������ã�*/
    @NoState
    public void setOnLoadDataEL(String onLoadDataEL) {
        this.getProperty(Propertys.onLoadDataEL.name()).value(onLoadDataEL);
    }
    private MethodExpression loadDataExp = null;
    /**��ȡloadDataExp���Ե�{@link MethodExpression}����*/
    public MethodExpression getOnLoadDataExpression() {
        if (this.loadDataExp == null) {
            String loadDataExpString = this.getOnLoadDataEL();
            if (loadDataExpString == null || loadDataExpString.equals("")) {} else
                this.loadDataExp = new MethodExpression(loadDataExpString);
        }
        return this.loadDataExp;
    }
    /**�����¼�ʱ��URL����Я���Ĳ�������RW��*/
    public String getAjaxParam() {
        return this.getProperty(Propertys.ajaxParam.name()).valueTo(String.class);
    }
    /**�����¼�ʱ��URL����Я���Ĳ�������RW��*/
    public void setAjaxParam(String ajaxParam) {
        this.getProperty(Propertys.ajaxParam.name()).value(ajaxParam);
    }
    /*-------------------------------------------------------------------------------���ķ���*/
    /**��ȡ���ڸ��ӵ����Ե�Map����*/
    public Map<String, Object> getAtts() {
        return this.atts;
    };
    /**��ȡ�齨���ͣ�ÿһ��UI�齨��Ӧ�þ߱�һ����һ�޶���componentType�����ID��������ʾ�齨���͡�*/
    public abstract String getComponentType();
    /**��ȡ�齨���齨���е�λ�ø�ʽΪ��/1/3/4/2 */
    public String getComponentPath() {
        if (this.componentPath == null) {
            StringBuffer buffer = new StringBuffer("/");
            UIComponent target = this;
            UIComponent targetParent = target.getParent();
            while (targetParent != null) {
                int index = targetParent.getChildren().indexOf(target);
                buffer.append(new StringBuffer(String.valueOf(index)).reverse());
                buffer.append('/');
                //
                target = targetParent;
                targetParent = target.getParent();
            }
            if (buffer.length() > 1)
                this.componentPath = buffer.deleteCharAt(0).reverse().toString();
            else
                this.componentPath = buffer.reverse().toString();
        }
        return this.componentPath;
    }
    /**��ȡһ�����õĿͻ���ID*/
    public String getClientID(ViewContext viewContext) {
        if (this.getComponentID() != null)
            return getComponentID();
        else
            return "uiCID_" + viewContext.getComClientID(this);
    }
    public UIComponent getChildByPath(String componentPath) {
        if (componentPath == null || componentPath.equals("") == true)
            return null;
        String thisPath = this.getComponentPath();
        if (thisPath.equals(componentPath) == true)
            return this;//�ж�Ŀ���Ƿ�����Լ���
        if (componentPath.startsWith(thisPath) == false)
            return null;//�ų�Ҫ��ȡ��Ŀ�겻���Լ����ӵ������
        //
        String targetPath = componentPath.substring(thisPath.length());
        int firstSpan = targetPath.indexOf('/');
        {
            if (firstSpan == 0) {
                targetPath = targetPath.substring(1);
                firstSpan = targetPath.indexOf('/');
            }
        }
        //
        int index = -1;
        if (firstSpan == -1)
            index = Integer.parseInt(targetPath);
        else
            index = Integer.parseInt(targetPath.substring(0, firstSpan));
        //
        UIComponent comObject = this.getChildren().get(index);
        //
        if (comObject == null)
            return null;
        else
            return comObject.getChildByPath(componentPath);
    }
    /**�ڵ�ǰ������Ӽ���Ѱ��ĳ���ض�ID�����*/
    public UIComponent getChildByID(String componentID) {
        if (componentID == null)
            return null;
        if (this.getComponentID().equals(componentID) == true)
            return this;
        for (UIComponent component : this.components) {
            UIComponent com = component.getChildByID(componentID);
            if (com != null)
                return com;
        }
        return null;
    };
    /**��ȡһ��int����ֵ������ǰ����й��ж��ٸ���Ԫ��*/
    public int getChildCount() {
        return this.components.size();
    };
    /**��ȡһ��Ԫ�ؼ��ϣ��ü����Ǵ��������ĳ���*/
    public List<UIComponent> getChildren() {
        return Collections.unmodifiableList(this.components);
    };
    /**��ȡһ���齨�б����б��а����˸��齨�Լ����齨���������齨��*/
    public List<UIComponent> getALLChildren() {
        ArrayList<UIComponent> list = new ArrayList<UIComponent>();
        list.add(this);
        for (UIComponent uic : components)
            list.addAll(uic.getALLChildren());
        return list;
    };
    /**�������齨*/
    public void addChildren(UIComponent componentItem) {
        componentItem.setParent(this);
        this.components.add(componentItem);
    };
    /**��ȡ�齨�ĸ�����*/
    public UIComponent getParent() {
        return this.parent;
    };
    /**�����齨�ĸ�����*/
    private void setParent(UIComponent parent) {
        this.parent = parent;
    }
    /**��ȡ�������Եļ��ϡ�*/
    public Map<String, AbstractValueHolder> getPropertys() {
        return this.propertys;
    };
    /**��ȡ���ڱ�ʾ������Զ���*/
    public AbstractValueHolder getProperty(String propertyName) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        if (value == null)
            return new StaticValueHolder();
        return value;
    };
    /**����һ��EL��ʽ���齨�����Բ���readString��writeString�ֱ��Ӧ��ҵ���齨�Ķ�д���ԡ�*/
    public void setPropertyEL(String propertyName, String readString, String writeString) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        ExpressionValueHolder elValueHolder = null;
        if (value == null || value instanceof ExpressionValueHolder == false)
            elValueHolder = new ExpressionValueHolder(readString, writeString);
        this.getPropertys().put(propertyName, elValueHolder);
    };
    /**�÷����ὫelString��������ΪreadString�͡�writeString��*/
    public void setPropertyEL(String propertyName, String elString) {
        this.setPropertyEL(propertyName, elString, elString);
    };
    /**�����齨���Ե�ֵ����ֵ������ֻ��Ӱ�챾�������������ڣ���*/
    public void setProperty(String propertyName, Object newValue) {
        if (ViewContext.getCurrentViewContext().getPhaseID().equals(InitView_Phase.PhaseID) == true)
            throw new RuntimeException("�벻Ҫ��InitView�׶�ʹ�ø÷�����");
        //
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        if (value == null)
            value = new StaticValueHolder();
        value.value(newValue);
        this.getPropertys().put(propertyName, value);
    };
    /**�����齨���Ե�MetaValueֵ����ֵ������Ϊ������ȫ���߳��ϵ�Ĭ�ϳ�ʼ��ֵ�����������ϵ�Ĭ��ֵ����
     * ע�⣺��initUIComponent������ʹ�ø÷���ֻ��Ӱ�쵽��Щδ��ҳ���ж�������ԡ�*/
    public void setPropertyMetaValue(String propertyName, Object newValue) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        if (value == null)
            value = new StaticValueHolder(newValue);
        this.getPropertys().put(propertyName, value);
        //������init�����е���������
        ViewContext view = ViewContext.getCurrentViewContext();
        if (view != null && view.getPhaseID().equals(InitView_Phase.PhaseID) == false)
            value.setMetaValue(newValue);
    };
    /**��map�е�����ȫ����װ����ǰ�齨��*/
    public void setupPropertys(Map<String, Object> objMap) {
        if (objMap != null)
            for (String key : this.propertys.keySet())
                if (objMap.containsKey(key) == true) {
                    AbstractValueHolder vh = this.propertys.get(key);
                    Object newValue = objMap.get(key);
                    vh.value(newValue);
                }
    };
    /*-------------------------------------------------------------------------------��������*/
    /**�齨����ʼ�����*/
    private Boolean doInit = false;
    /**��1�׶Σ�������ʼ���׶Σ��ý׶θ����ʼ�������*/
    public final void processInit(ViewContext viewContext) throws Throwable {
        if (this.doInit == false) {
            this.initUIComponent(viewContext);
            this.doInit = true;
        }
        /*�������ԣ��������Իᱣ֤ÿ�����������ڵ�����ֵ����UI�ж����ԭʼֵ��*/
        for (AbstractValueHolder vh : this.propertys.values())
            vh.reset();
        for (UIComponent com : this.components)
            com.processInit(viewContext);
    };
    /**��3�׶Σ��������������������һ�µ����Թ��������ϡ�*/
    public void processApplyRequest(ViewContext viewContext) throws Throwable {
        /*�����������Ҫ����������ֵ���뵽������*/
        for (String key : this.propertys.keySet()) {
            /*�����������������������б����ǡ�componentID:attName��*/
            String[] newValues = viewContext.getHttpRequest().getParameterValues(this.getComponentPath() + ":" + key);
            if (newValues == null)
                continue;
            else if (newValues.length == 1)
                this.propertys.get(key).value(newValues[0]);
            else
                this.propertys.get(key).value(newValues);
        }
        for (UIComponent com : this.components)
            com.processApplyRequest(viewContext);
    };
    /**��4�׶Σ��ý׶������ṩһ����֤���ݵĺϷ��ԡ�*/
    public void processValidate(ViewContext viewContext) throws Throwable {
        for (UIComponent com : this.components)
            com.processValidate(viewContext);
    };
    /**��5�׶Σ������ģ���е���ֵӦ�õ���Bean*/
    public void processUpdate(ViewContext viewContext) throws Throwable {
        /*��������ע�ᵽpropertys�е�����ֵ*/
        for (String key : this.propertys.keySet()) {
            AbstractValueHolder vh = this.propertys.get(key);
            if (vh.isUpdate() == true)
                vh.updateModule(this, viewContext);
        }
        for (UIComponent com : this.components)
            com.processUpdate(viewContext);
    };
    /**��6�׶Σ�����Action�����Ϳͻ��˻ش����¼�*/
    public void processApplication(ViewContext viewContext) throws Throwable {
        if (this.getComponentPath().equals(viewContext.getTargetPath()) == true) {
            /*�����ͻ�����������*/
            Event eventType = Event.getEvent(viewContext.getEvent());
            if (eventType != null)
                /**�¼�����*/
                this.doEvent(eventType, viewContext);
        }
        for (UIComponent com : this.components)
            com.processApplication(viewContext);
    };
    /*-------------------------------------------------------------------------------�¼���Ӧ*/
    /**ִ���¼�*/
    protected void doEvent(Event eventType, ViewContext viewContext) throws Throwable {
        try {
            for (Event e : this.listener.keySet())
                if (e.equals(eventType) == true) {
                    List<EventListener> listeners = this.listener.get(eventType);
                    for (EventListener listener : listeners)
                        listener.onEvent(eventType, this, viewContext);
                }
        } catch (Exception e) {
            if (viewContext.isAjax() == true) {
                viewContext.sendError(e);
                //e.printStackTrace(System.err);
            } else
                throw e;
        }
    };
    /**����һ�������¼����¼���������*/
    public void addEventListener(Event eventType, EventListener listener) {
        if (eventType == null || listener == null)
            return;
        List<EventListener> listeners = this.listener.get(eventType);
        if (listeners == null) {
            //            Log.debug("this event is first append, event = " + eventType + ", listener = " + listener);
            listeners = new ArrayList<EventListener>();
            this.listener.put(eventType, listeners);
        }
        //        Log.debug("add event listener, event = " + eventType + ", listener = " + listener);
        listeners.add(listener);
    };
    /*-------------------------------------------------------------------------------״̬����*/
    /**��״̬�����лָ��齨״̬*/
    public void restoreState(List<?> stateData) {
        //1.���ݼ��
        if (stateData == null)
            return;
        if (stateData.size() == 0)
            throw new DataException("WebUI�޷��������״̬�����������[" + this.getComponentID() + "]����������ݶ�ʧ");
        //2.�ָ���������
        Map<String, Object> mineState = (Map<String, Object>) stateData.get(0);
        for (String propName : mineState.keySet()) {
            /*�ų�����*/
            if (propName == null)
                continue;
            /*ID���Բ�����*/
            if (propName.toLowerCase().equals("id") == true)
                continue;
            /*����ע��*/
            Method rm = BeanUtils.getWriteMethod(propName, this.getClass());
            if (rm == null)
                continue;
            if (rm.getAnnotation(NoState.class) != null)
                continue;
            /*д������*/
            AbstractValueHolder vh = this.propertys.get(propName);
            if (vh != null)
                vh.value(mineState.get(propName));
        }
        //3.�ָ������
        if (stateData.size() == 2) {
            Map<String, Object> childrenState = (Map<String, Object>) stateData.get(1);
            for (UIComponent com : components)
                com.restoreState((List<?>) childrenState.get(com.getComponentPath()));
        }
    };
    /**�����齨�ĵ�ǰ״̬�����������齨��*/
    public List<Object> saveStateOnlyMe() {
        //1.�־û�������״̬
        HashMap<String, Object> mineState = new HashMap<String, Object>();
        for (String propName : this.propertys.keySet()) {
            Method rm = BeanUtils.getReadMethod(propName, this.getClass());
            if (rm == null)
                continue;
            if (rm.getAnnotation(NoState.class) != null)
                continue;
            AbstractValueHolder vh = this.propertys.get(propName);
            mineState.put(propName, vh.value());
        }
        //3.���س־û�״̬
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(mineState);
        return array;
    };
    /**�����齨�ĵ�ǰ״̬���������齨��*/
    public List<Object> saveState() {
        //1.�־û�������״̬
        List<Object> array = this.saveStateOnlyMe();
        //2.�־û��������״̬
        HashMap<String, Object> childrenState = new HashMap<String, Object>();
        for (UIComponent com : components)
            childrenState.put(com.getComponentPath(), com.saveState());
        //3.���س־û�״̬
        array.add(childrenState);
        return array;
    };
};
/**������OnLoadData�¼���EL����*/
class Event_OnLoadData implements EventListener {
    public void onEvent(Event event, UIComponent component, ViewContext viewContext) throws Throwable {
        MethodExpression e = component.getOnLoadDataExpression();
        if (e != null)
            viewContext.sendObject(e.execute(component, viewContext));
    }
}