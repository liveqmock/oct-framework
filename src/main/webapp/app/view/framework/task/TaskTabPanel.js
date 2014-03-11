//新闻TabPane面板
Ext.define("Wms.view.framework.task.TaskTabPanel", {
    extend: "Ext.tab.Panel",
    alias: "widget.tasktabpanel",

    //面板默认配置项
    resizeTabs: true,
    enableTabScroll: true,
    activeTab: 0,

    //面板布局需要的组件
    requires: [
        "Wms.view.framework.task.task.TaskPanel",
        "Wms.view.framework.task.log.TaskLogPanel"
    ],


    initComponent: function () {
        var me = this;
        me.items = [
        ];
        this.callParent()
    }
});
