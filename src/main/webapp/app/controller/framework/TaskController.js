/**
 * 任务控制器，
 */


Ext.define("Wms.controller.framework.TaskController", {
    extend: "Ext.app.Controller",


    //要用到的视图，模型，数据,
    views: ["framework.task.View"],
    models: ["framework.task.TaskModel","framework.task.TaskLogModel"],
    stores: ["framework.task.TaskStore","framework.task.TaskLogStore"],

    //根据ref属性创建getXXXX方法。用于获取到对应的视图组件
    refs: [
        {
            ref: "taskview",
            selector: "taskview"
        },
        //任务列表
        {
            ref: "tasklist",
            selector: "tasklist"
        },
        {
            ref: "taskloglist",
            selector: "taskloglist"
        },
        {
            ref: "tasktabpanel",
            selector: "tasktabpanel"
        },
        {
            ref: "taskpanel",
            selector: "taskpanel"
        },
        {
            ref: "tasklogpanel",
            selector: "tasklogpanel"
        },
        {
            ref: "taskserach",
            selector: "taskserach"
        }
    ],


    //自定义属义------------------------------------------
    //创建一个已打开的Panels集合，所以已打开的面板都会以key---value的方法存入此集合中。
    openedPanels: Ext.create("Ext.util.MixedCollection"),


    //初始化控制，注册各类事件
    init: function () {
        var me = this;
        me.control({
            'tasklist': {

                //视图加载前 添加操作
                beforerender: function (panel) {
                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
                    if (btns != null) {
                        panel.down("toolbar").add("->");
                        for (var i = 0; i < btns.length; i++) {

                            panel.down("toolbar").add(btns[i]);
                        }
                    }
                }
            },
            'tasktabpanel': {
                beforerender: function (panel) {

                    //获取菜单集合
                    var tree = Wms.treeShow.fn(Wms.menuInfo.submenu,me);
                    var mypanel = new Ext.panel.Panel({
                        title: "欢迎页面",
                        resizeTabs: true,
                        enableTabScroll: true,
                        activeTab: 0,
                        iconCls: "demoSmall",
                        items: [
                            {
                                baseCls:'maintree',
                                xtype: 'fieldset',
                                title: Wms.menuInfo.name,
                                anchor: '100%',
                                style: {
                                background:"#fff",
                                border: "0px solid #e8e8e8"
                                },
                                items:tree
                            }
                        ]

                    });
                    panel.add(mypanel);
                }
            },

            'tasklogList': {
                //视图加载前 添加操作
                beforerender: function (panel) {
                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
                    if (btns != null) {
                        panel.down("toolbar").add("->");
                        for (var i = 0; i < btns.length; i++) {
                            panel.down("toolbar").add(btns[i]);
                        }
                    }
                }
            },
            'taskview': {
                beforerender: function (panel) {
                    panel.down("toolbar").add(Wms.menuShow.fn(Wms.menuInfo.submenu));
                }
            },
            'taskview [action=taskPanel], [action=taskLogPanel]': {
                click: this.onShowMenuTabPanel
            },
            //暂停回复trigger
            'tasklist [action=pauseTask]': {
                click: me.pauseTask
            }

        });

    },

    onLaunch: function () {
    },

    onShowMenuTabPanel: function (btn) {
        var me = this,
            panel,
            panelAction = btn.action,//请求
            panelCls=btn.menuInfo.menuSmallcls,//图标
            panelTitle = btn.text;//name
        panel = me.openedPanels.get(panelAction);
        if (panel) {
            panel.isHidden() ? panel.show() : null;
        } else {
            if (panelAction == "taskPanel") {
                panel = me.createtabPanel(panelTitle, panelAction,panelCls);
                Ext.StoreManager.lookup("framework.task.TaskStore").load();
            } else if (panelAction == "taskLogPanel") {
                panel = me.createtabPanel(panelTitle,panelAction,panelCls);
                Ext.StoreManager.lookup("framework.task.TaskLogStore").load();
            }
            me.getTasktabpanel().add(panel).show();
            me.openedPanels.add(panelAction, panel);
        }
    },

    //创建一个新的Panel
    createtabPanel: function (title, widget, cls) {
        var me = this;
        var panel = Ext.widget(widget, {
            title: title,
            closable: true, //可关闭?
            iconCls: cls,//图标
            overflowY: "auto" //自动添加滚动?
        });
        panel.on({
            destroy: function (p) {
                me.openedPanels.remove(p);
            }
        });
        return panel;
    },

    /**
     * 暂停恢复trigger
     * @param btn
     */
    pauseTask: function (btn) {
        var me=this;
        var rec = me.getTasklist().getSelectionModel().getSelection();
        if(rec.length<=0){
            Ext.MessageBox.alert({
                title: '错误提示',
                msg: '请选择要重发的项',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.INFO
            });
            return;
        }
        if(rec.length>1){
            Ext.MessageBox.alert({
                title: '错误提示',
                msg: '只能选择一项',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        var now_status = rec[0].data["status"];
        var status;
        var msg;
        if(now_status==0){
            msg = "暂停";
            status=1;
        }else{
            msg ="恢复";
            status=0;
        }

        Ext.Msg.confirm(msg+"任务","确认"+msg+"任务？",function(btn){
            if(btn==="yes"){
                Ext.Ajax.request({
                    method:"GET",
                    url:Ext.StoreManager.lookup("framework.task.TaskStore").getProxy().api.updateTriggerStatus,
                    params:{"tiggerName":rec[0].data["taskId"],"groupName":rec[0].data["groupName"],"status":status},
                    success:function(response,opts){
                        Ext.Msg.alert("提示","操作成功");
                        Ext.StoreManager.lookup("framework.task.TaskStore").load();
                        me.getTasklist().getSelectionModel().clearSelections();
                    },
                    failure: function (e, a) {
                        Ext.Msg.alert("错误", a.result.message);
                    }
                });

            }
        });
    }


    //搜索功能
    /*    onSerachGroup:function(btn){
     panelName=btn.action;
     //尝试在集合中获取面板
     panel=this.openedPanels.get(panelName);
     //搜索按钮事件
     panel.down("button[action=groupSearch]").on("click",function(){
     var form =panel.getForm();
     if(form.isValid &&form.isDirty()){
     form.submit({
     method:"GET",
     //  params:{"listCheckbox":me.getCheckboxtree().checklist},
     //   url:me.getGroupStoreStore().getProxy().api.query+"/1/10.json",
     url:me.getGroupStoreStore().getProxy().api.read,
     success: function (f, a) {
     },
     //失败
     failure: function (e, opt) {
     }

     });

     }

     });
     }*/

    //提


});
