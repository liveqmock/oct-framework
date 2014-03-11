/**
 * 分组中心主视图
 */

Ext.define("Wms.view.framework.task.View",{
    extend:"Ext.panel.Panel",
    alias:"widget.taskview",

    //布局时，需要加载的视图组件
    requires:[
        "Wms.view.framework.task.TaskTabPanel"
    ],


    //自适应容器
    layout:"fit",

    //初始化视图，布局开始
    initComponent:function(){

        var me=this;
        me.items = [
            {
                xtype : "tasktabpanel",
                flex : 1
            } ];

        me.tbar={
            xtype:"toolbar",
            defaultType:"button",
            style:"padding-left:0",
            //菜单按钮默认样式设置
            defaults: {
                cls:"navMenu"
            },
            cls:"navMenuBox",
            items:[]
        };

        this.callParent();
    }


});
