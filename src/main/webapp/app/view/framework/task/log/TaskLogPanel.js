
//新闻TabPane面板
Ext.define("Wms.view.framework.task.log.TaskLogPanel",{
    extend:"Ext.panel.Panel",
    alias:"widget.taskLogPanel",
    layout:"fit",
    //面板布局需要的组件
    requires:[
        "Wms.view.framework.task.log.TaskLogSearch",
        "Wms.view.framework.task.log.TaskLogList"
    ],


    initComponent:function(){
        var me = this;
        me.items=[
            {

                layout: "border",
                items: [
                    {
                        xtype: "tasklogserach",
                        region: "west",
                        width:220,
                        collapsed: false,
                        title: "查询",
                        split: true,
                        collapsible: true
                    },
                    {
                        region: "center",
                        flex: 1,
                        xtype: "taskloglist"
                    }
                ]
            }
        ];

        this.callParent()
    }
});
