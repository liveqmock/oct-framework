
//新闻TabPane面板
Ext.define("Wms.view.framework.task.task.TaskPanel",{
    extend:"Ext.panel.Panel",
    alias:"widget.taskPanel",
    layout:"fit",
    //面板布局需要的组件
    requires:[
        "Wms.view.framework.task.task.TaskSearch",
        "Wms.view.framework.task.task.TaskList"
    ],


    initComponent:function(){
        var me = this;
        me.items=[
            {

                layout: "border",
                items: [
                    {
                        xtype: "taskserach",
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
                        xtype: "tasklist"
                    }
                ]
            }
        ];

        this.callParent()
    }
});
