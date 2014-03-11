
//新闻TabPane面板
Ext.define("Wms.view.framework.group.grouptype.GroupTypePanel",{

    extend:"Ext.panel.Panel",
    alias:"widget.groupTypePanel",
    layout:"fit",

    //面板布局需要的组件
    requires:[
        "Wms.view.framework.group.grouptype.GroupTypeSearch",
        "Wms.view.framework.group.grouptype.GroupTypeList",
        "Wms.view.framework.group.grouptype.GroupTypeAdd",
        "Wms.view.framework.group.grouptype.GroupTypeEdit"
    ],


    initComponent:function(){

         this.items=[
             //新闻列表
             {
                 layout:"border",
                 items:[
                     {
                         xtype:"groupTypeSearch",
                         region: "west",
                         width:220,
                         collapsed: false,
                         title: "查询",
                         split: true,
                         collapsible: true
                     },
                     {
                         region:"center",
                         flex:1,
                         xtype:"groupTypeList"
                     }
                 ]
             }

         ];

        this.callParent()
    }
});
