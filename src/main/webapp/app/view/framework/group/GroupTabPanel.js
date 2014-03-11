//新闻TabPane面板
Ext.define("Wms.view.framework.group.GroupTabPanel", {
    extend: "Ext.tab.Panel",
    alias: "widget.grouptabpanel",

    //面板默认配置项
    resizeTabs: true,
    enableTabScroll: true,
    activeTab: 0,

    //面板布局需要的组件
    requires: [
        "Wms.view.framework.group.group.GroupPanel",
        "Wms.view.framework.group.grouptype.GroupTypePanel",
        "Wms.view.framework.util.CheckboxTree",
        "Wms.view.framework.util.TreeComboBox"

    ],


    initComponent: function () {
        var me = this;
        //console.info(me.down("toolbar"));
        me.items = [
/*            {
                title: "欢迎界面",
                iconCls: "demoSmall",
                html: "<h1 style='text-align: center;'>业务管理模块</h1></br>" +
                    "<p style='text-align: center;'>" +
                    "包含平台管理、景区管理、渠道管理、业务系统级别管理、CRM会员级别管理</br>" +
                    "</br></br>&nbsp;&nbsp;如果您遇到问题，请联系管理人员联系。" +
                    "<p>"
            },*/
            /*{
                title: "欢迎界面",
                iconCls: "demoSmall",
                items: [
                    {
                        xtype: 'fieldset',
                        title: '菜单目录',
                        anchor: '100%',
                        style: {
//                          background: "#e8e8e8",
                            border: "2px solid #e8e8e8"
                        },
                        items: [
                            {
                                xtype: 'container',
                                anchor: '100%',
                                layout: 'hbox',
                                items: [
                                    {
                                        xtype: 'container',
                                        flex: 1,
                                        layout: 'anchor',
                                        items: [
                                            {
                                                xtype:'checkboxtree',
                                                store:Ext.create('Wms.store.framework.TreeStore', {
                                                    model: "Wms.model.framework.TreeModel",
                                                    proxy: {
                                                        type: 'ajax',
                                                        url: 'role/getMenuTree.json?parentRoleId='+0
                                                    }
                                                })
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'container',
                                        flex: 1,
                                        layout: 'anchor',
                                        items: [
                                            {
                                                xtype:'checkboxtree',
                                                store:Ext.create('Wms.store.framework.TreeStore', {
                                                    model: "Wms.model.framework.TreeModel",
                                                    proxy: {
                                                        type: 'ajax',
                                                        url: 'role/getMenuTree.json?parentRoleId='+0
                                                    }
                                                })
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'container',
                                        flex: 1,
                                        layout: 'anchor',
                                        items: [
                                            {
                                                xtype:'checkboxtree',
                                                store:Ext.create('Wms.store.framework.TreeStore', {
                                                    model: "Wms.model.framework.TreeModel",
                                                    proxy: {
                                                        type: 'ajax',
                                                        url: 'role/getMenuTree.json?parentRoleId='+0
                                                    }
                                                })
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'container',
                                        flex: 1,
                                        layout: 'anchor',
                                        items: [
                                            {
                                                xtype:'checkboxtree',
                                                store:Ext.create('Wms.store.framework.TreeStore', {
                                                    model: "Wms.model.framework.TreeModel",
                                                    proxy: {
                                                        type: 'ajax',
                                                        url: 'role/getMenuTree.json?parentRoleId='+0
                                                    }
                                                })
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }*/

        ];
        this.callParent()
    }
});
