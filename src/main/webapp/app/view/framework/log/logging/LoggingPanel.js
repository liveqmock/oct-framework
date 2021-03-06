
//新闻TabPane面板
Ext.define("Wms.view.framework.log.logging.LoggingPanel",{
    extend:"Ext.panel.Panel",
    alias:"widget.loggingPanel",
    //面板默认配置项
    layout:"fit",
    //面板布局需要的组件
    requires:[
        "Wms.view.framework.log.logging.LoggingSearch",
        "Wms.view.framework.log.logging.LoggingList"
    ],
    initComponent:function(){

         this.items=[
             //登录日志列表
             {
                 layout:"border",
                 items:[
                     {
                         region:"west",
                         width:220,
                         title:"日志查询",
                         xtype:"loggingSearch",
                         split:true,
                         collapsible:true
                     },
                     {
                         region:"center",
                         flex:1,
                         xtype:"loggingList"
                     }
                 ]
             }

         ];
         this.callParent()
    }
});
