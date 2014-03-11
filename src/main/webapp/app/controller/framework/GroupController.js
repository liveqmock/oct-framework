/**
 * 分组控制器，分组业务处理，
 */


Ext.define("Wms.controller.framework.GroupController", {
    extend: "Ext.app.Controller",


    //要用到的视图，模型，数据,
    views: ["framework.group.View"],
    models: ["framework.group.GroupModel", "framework.TreeModel", "framework.role.RoleListModel"],
    stores: ["framework.group.GroupStore", "framework.TreeStore", "framework.role.GroupTypeListStore"],

    requires: [
        "Wms.utils.framework.RoleUtils"
    ],

    //根据ref属性创建getXXXX方法。用于获取到对应的视图组件
    refs: [
        //分组主视图
        {
            ref: "groupview",
            selector: "groupview"
        },

        //分组列表
        {
            ref: "grouplist",
            selector: "grouplist"
        },


        {
            ref: "grouptabpanel",
            selector: "grouptabpanel"
        },
        {
            ref: "groupPanel",
            selector: "groupPanel"
        },
        //添加面板
        {
            ref: "groupaddview",
            selector: "groupaddview"
        },
        //编辑面板
        {
            ref: "groupeditview",
            selector: "groupeditview"
        },
        //搜索面板
        {
            ref: "groupserach",
            selector: "groupserach"
        },
        {
            ref: "roleview",
            selector: "roleview"
        },

        //角色列表
        {
            ref: "groupTypeList",
            selector: "groupTypeList"
        },

        //角色tab panel
        {
            ref: "groupTypePanel",
            selector: "groupTypePanel"
        },

        //树面板
        {
            ref: "checkboxtree",
            selector: "checkboxtree"
        }
    ],


    //自定义属义------------------------------------------
    //创建一个已打开的Panels集合，所以已打开的面板都会以key---value的方法存入此集合中。
    openedPanels: Ext.create("Ext.util.MixedCollection"),


    //初始化控制，注册各类事件
    init: function () {
        var me = this;
        var roleUtils = Ext.create('Wms.utils.framework.RoleUtils');

        me.control({
            'grouplist': {

                //视图加载前 添加操作
                beforerender: function (panel) {
                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
                    if (btns != null) {
                        for (var i = 0; i < btns.length; i++) {

                            panel.down("toolbar").add(btns[i]);
                            if (i == 0) {
                                panel.down("toolbar").add("->");
                            }
                        }
                    }
                }
            },
            'grouptabpanel': {
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

            'groupTypeList': {

                //视图加载前 添加操作
                beforerender: function (panel) {
                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
                    if (btns != null) {
                        for (var i = 0; i < btns.length; i++) {

                            panel.down("toolbar").add(btns[i]);
                            if (i == 0) {
                                panel.down("toolbar").add("->");
                            }
                        }
                    }
                }
            },
            'groupview': {
                beforerender: function (panel) {
                    panel.down("toolbar").add(Wms.menuShow.fn(Wms.menuInfo.submenu));
                }
            },
            'groupview [action=groupPanel], [action=groupTypePanel]': {
                click: this.onShowMenuTabPanel
            },
            //添加分组
            'grouplist [action=addGroup]': {
                click: me.onShowAddPanel
            },

            //编辑分组
            'grouplist [action=editGroup]': {
                click: me.onShowEditPanel
            },

            //删除分组
            'grouplist [action=delGroup]': {
                click: me.onDelGroup
            },

            //添加分组类型
            'groupTypeList [action=addRole]': {
                click: me.onShowGroupTypeAddPanel
            },

            //编辑分组类型
            'groupTypeList [action=editRole]': {
                click: me.onShowGroupTypeEditPanel
            },

            //删除分组类型
            'groupTypeList [action=delRole]': {
                click: me.onGroupTypeDelRole
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
            if (panelAction == "groupPanel") {
                panel = me.createtabPanel(panelTitle, panelAction,panelCls);
                Ext.StoreManager.lookup("framework.group.GroupStore").load();
            } else if (panelAction == "groupTypePanel") {
                panel = me.createtabPanel(panelTitle,panelAction,panelCls);
                Ext.StoreManager.lookup("framework.role.GroupTypeListStore").load();
            }
            me.getGrouptabpanel().add(panel).show();
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

    //添加功能
    onShowAddPanel: function (btn) {
        var me = this;
        var panelName, panel;
        if (btn.hasOwnProperty("action")) {
            panelName = btn.action;
            //尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);

            if (panel) {
                //面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {
                //面板不存在，创建
                panel = Ext.widget("groupaddview", {
                    title: "添加分组",
                    closable: true, //可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto" //自动添加滚动条
                });

                //显示面板，将面板添加入打开的面板集合中.
                me.getGrouptabpanel().add(panel).show();
                me.openedPanels.add(panelName, panel);
                //console.log(me.openedPanels);


                //获取新闻模型类            获取到表单对象
                //    var groupModel=me.getGroupModelModel();
                var groupModel = Ext.create("Wms.model.framework.group.GroupModel");
                var form = panel.getForm();
                form.loadRecord(groupModel);


                //提交按钮事件
                panel.down("button[action=groupAddSubmit]").on("click", function () {
                    if (form.isValid() && form.isDirty()) {
                        //创建一条记录，
                        var newRec = form.getRecord();
                        //更新记录内容
                        form.updateRecord(newRec);
                        form.submit({
                            method: "POST",
                            url: Ext.StoreManager.lookup("framework.group.GroupStore").getProxy().api.create,
                            success: function (f, a) {
                                Ext.Msg.alert("成功", "添加分组成功");
                                Ext.getCmp('groupAddView').close();
                                Ext.StoreManager.lookup("framework.group.GroupStore").load();
                                Wms.refresh.fn();//刷新桌面
                            },
                            failure: function (e, a) {
                                Ext.Msg.alert("错误", a.result.message);
                            }

                        });

                    } else {
                        Ext.Msg.alert("提示", "表单不能为空！");
                    }

                });

                //重置按钮
                panel.down("button[action=groupReset]").on("click", function () {
                    form.reset();
                });

                //关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });


            }
        }
    },

    onShowGroupTypeAddPanel: function (btn) {
        var me = this;
        var panelName, panel;
        var parentRoleId = 0;
        var rec = me.getGroupTypeList().getSelectionModel().getSelection();
        panelName = "添加类型";
        if (rec.length <= 0) {
            //如果没有选择角色，默认添加当前面板父角色的子角色
            parentRoleId = me.nowParentRoleId;
            //如果是admin，则添加顶级角色
            /*if(Ext.util.Cookies.get("userName")=='YWRtaW4='){
             parentRoleId=0;
             panelName='添加角色';
             }*/
        } else {
            parentRoleId = rec[0].data["roleId"];
        }
        if (btn.hasOwnProperty("action")) {
            panelName = btn.action;

            //尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);

            if (panel) {
                //面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {
                //面板不存在，创建
                panel = Ext.widget("groupTypeAddView", {
                    title: "添加类型",
                    closable: true, //可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto",//自动添加滚动条
                    parentRoleId: 0
                });

                //显示面板，将面板添加入打开的面板集合中.
                me.getGrouptabpanel().add(panel).show();
                me.openedPanels.add(panelName, panel);


                //获取新闻模型类            获取到表单对象
                //    var roleListModel=me.get_RoleListModelModel();
                var roleListModel = Ext.create("Wms.model.framework.role.RoleListModel");
                var form = panel.getForm();
                form.loadRecord(roleListModel);


                panel.isShowView();

                //提交按钮事件
                panel.down("button[action=roleAddSubmit]").on("click", function () {
                    if (form.isValid() && form.isDirty()) {
                        //创建一条记录，
                        var newRec = form.getRecord();
                        //更新记录内容
                        form.updateRecord(newRec);
                        form.submit({
                            method: "POST",
                            params: {"listCheckbox": me.getCheckboxtree().getChecklist(), "parentRoleId": parentRoleId},
                            url: Ext.StoreManager.lookup("framework.role.GroupTypeListStore").getProxy().api.create,
                            success: function (f, a) {
                                Ext.Msg.alert("成功", "新增类型成功");
                                Ext.getCmp('roleAddForm').close();
                                Ext.StoreManager.lookup("framework.role.GroupTypeListStore").load();
                                Wms.refresh.fn();//刷新桌面
                            },
                            //失败
                            failure: function (e, a) {
                                Ext.Msg.alert("错误", a.result.message);
                            }

                        });

                    } else {
                        Ext.Msg.alert("提示", "表单不能为空！");
                    }

                });

                //重置按钮
                panel.down("button[action=roleReset]").on("click", function () {
                    form.reset();
                });

                //关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });


            }
        }
    },

    onShowGroupTypeEditPanel: function (btn) {
        var me = this;
        var panelName, panel;
        if (btn.hasOwnProperty("action")) {

            panelName = btn.action;
            var rec = me.getGroupTypeList().getSelectionModel().getSelection();
            if (rec.length <= 0) {
                return;
            }
            if (rec.length > 1) {
                Ext.Msg.alert("错误", "不可编辑多个类型！");
                return;
            }
            panelName = panelName + rec[0].data["roleId"];


            //尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);

            if (panel) {
                //面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {

                //面板不存在，创建
                //添加角色面板
                panel = Ext.widget("groupTypeEditView", {
                    title: "编辑" + rec[0].data["roleName"],
                    closable: true, //可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto", //自动添加滚动条
                    roleId: rec[0].data["roleId"],
                    parentRoleId: rec[0].data["parentRoleId"]
                });
                //显示面板，将面板添加入打开的面板集合中.
                me.getGrouptabpanel().add(panel).show();
                me.openedPanels.add(panelName, panel);


                // 获取到表单对象
                var form = panel.getForm();
                form.loadRecord(rec[0]);


                //提交按钮事件
                panel.down("button[action=roleEditSubmit]").on("click", function () {
                    if ((form.isValid() && form.isDirty()) || me.getCheckboxtree().isChanged) {
                        //创建一条记录，
                        var newRec = form.getRecord();
                        //更新记录内容
                        form.updateRecord(newRec);
                        form.submit({
                            method: "POST",
                            params: {"listCheckbox": me.getCheckboxtree().getChecklist(), "delMenuOperateIds": me.getCheckboxtree().getUnChecklist(), "roleId": rec[0].data["roleId"]},
                            url: Ext.StoreManager.lookup("framework.role.GroupTypeListStore").getProxy().api.update,
                            success: function (f, a) {
                                Ext.Msg.alert("成功", "编辑类型成功");
                                Ext.getCmp('roleEditForm').close();
                                Ext.StoreManager.lookup("framework.role.GroupTypeListStore").load();
                                Wms.refresh.fn();//刷新桌面
                            },
                            failure: function (e, a) {
                                Ext.Msg.alert("错误", a.result.message);
                            }

                        });
                    } else {
                        Ext.Msg.alert("提示", "没有修改动作！");
                    }


                });

                //重置按钮
                panel.down("button[action=roleReset]").on("click", function () {
                    form.reset();
                });


                //关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });


            }
        }
    },

    //编辑功能
    onShowEditPanel: function (btn) {
        var me = this;
        var panelName, panel;
        if (btn.hasOwnProperty("action")) {

            panelName = btn.action;
            var rec = me.getGrouplist().getSelectionModel().getSelection();
            if (rec.length <= 0) {
                return;
            }
            if (rec.length > 1) {
                Ext.Msg.alert("错误", "不可编辑多个分组！");
                return;
            }
            panelName = panelName + rec[0].data["groupId"];


            //尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);

            if (panel) {
                //面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {
                //面板不存在，创建
                //添加分组面板
                panel = Ext.widget("groupeditview", {
                    title: "编辑" + rec[0].data["groupName"],
                    closable: true, //可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto", //自动添加滚动条
                    groupId: rec[0].data["groupId"],
                    defaultGroupType: rec[0].data["groupType"],
                    parentGroupId: rec[0].data["parentGroupId"]
                });
                //显示面板，将面板添加入打开的面板集合中.
                me.getGrouptabpanel().add(panel).show();
                me.openedPanels.add(panelName, panel);


                // 获取到表单对象
                var form = panel.getForm();
                form.loadRecord(rec[0]);

                //提交按钮事件
                panel.down("button[action=groupEditSubmit]").on("click", function () {
                    if ((form.isValid() && form.isDirty())) {
                        //创建一条记录，
                        var newRec = form.getRecord();

                        //更新记录内容
                        form.updateRecord(newRec);
                        //在_NewsListStore中添加一条新记录
                        //       me.getGroupStoreStore().insert(0, newRec);
                        form.submit({
                            method: "POST",
                            params: {"groupId": rec[0].data["groupId"]},
                            url: Ext.StoreManager.lookup("framework.group.GroupStore").getProxy().api.update,
                            success: function (f, a) {
                                Ext.Msg.alert("成功", "编辑分组成功");
                                Ext.getCmp('groupEditView').close();
                                Ext.StoreManager.lookup("framework.group.GroupStore").load();
                                Wms.refresh.fn();//刷新桌面
                            },
                            failure: function (e, a) {
                                Ext.Msg.alert("错误", a.result.message);
                            }

                        });
                    } else {
                        Ext.Msg.alert("提示", "没有修改动作！");
                    }


                });

                //重置按钮
                panel.down("button[action=groupReset]").on("click", function () {
                    form.reset();
                });


                //关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });


            }
        }
    },


    onGroupTypeDelRole: function (btn) {
        var me = this;
        var rec = me.getGroupTypeList().getSelectionModel().getSelection();
        if (rec.length <= 0) {
            Ext.MessageBox.alert({
                title: '错误提示',
                msg: '请选择要删除的项',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        //提示是否删除
        var msg = ["确定删除以下类型吗？"];
        var ids = [];
        for (var i = 0, len = rec.length; i < len; i++) {
            ids.push(rec[i].data["roleId"]);
            msg.push(rec[i].data["roleName"]);
        }

        Ext.Msg.confirm("删除类型", msg.join("<br />"), function (btn) {
            if (btn === "yes") {
                Ext.Ajax.request({
                    url: Ext.StoreManager.lookup("framework.role.GroupTypeListStore").getProxy().api.destroy,
                    params: {"listCheckbox": ids},
                    success: function (response, opts) {
                        Ext.StoreManager.lookup("framework.role.GroupTypeListStore").loadPage(1);
                    },

                    failure: function (response, opts) {
                        Ext.Msg.alert("错误", '状态：' + response.status + ":" + response.responseText);
                    }
                });

            }
        });


    },

    //删除功能
    onDelGroup: function (btn) {
        var me = this;
        var rec = me.getGrouplist().getSelectionModel().getSelection();
        if (rec.length <= 0) {
            Ext.MessageBox.alert({
                title: '错误提示',
                msg: '请选择要删除的项',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        //提示是否删除
        var msg = ["确定删除以下分组吗？"];
        var ids = [];
        for (var i = 0, len = rec.length; i < len; i++) {
            ids.push(rec[i].data["groupId"]);
            msg.push(rec[i].data["groupName"]);
        }

        Ext.Msg.confirm("删除分组", msg.join("<br />"), function (btn) {
            if (btn === "yes") {

                //这里为直接启用了一个ajax请求来删除(演示效果使用)。
                // 开发时应该调用store.sync同步服务器的方法，来实现删除功能
                //var store=me.get_NewsListStoreStore();
                //store.sync({})
                Ext.Ajax.request({
                    method: "POST",
                    url: Ext.StoreManager.lookup("framework.group.GroupStore").getProxy().api.destroy,
                    params: {"listCheckbox": ids},
                    success: function (response, opts) {
                        Ext.StoreManager.lookup("framework.group.GroupStore").loadPage(1);
                    },

                    failure: function (response, opts) {
                        Ext.Msg.alert("错误", '状态：' + response.status + ":" + response.responseText);

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
