
//新闻TabPane面板
Ext.define("Wms.view.framework.group.group.GroupPanel",{
    extend:"Ext.panel.Panel",
    alias:"widget.groupPanel",
    layout:"fit",
    //面板布局需要的组件
    requires:[
        "Wms.view.framework.group.group.GroupSearch",
        "Wms.view.framework.group.group.GroupList",
        "Wms.view.framework.group.group.GroupAdd",
        "Wms.view.framework.group.group.GroupEdit"
    ],


    initComponent:function(){
        var me = this;
        me.items=[
            {

                layout: "border",
                items: [
                    {
                        xtype: "groupserach",
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
                        xtype: "grouplist"
                    }
                ]
            }
        ];

        this.callParent()
    }
});
