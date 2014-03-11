// 用户控制器
Ext.define('Wms.controller.framework.UserController', {
    extend: 'Ext.app.Controller',
    // 添加模型与数据与视图
    models: ['framework.user.UserListModel', 'framework.user.UserGroupModel','framework.TreeModel'],
    stores: ['framework.user.UserStore', 'framework.user.UserRolesStore', 'framework.user.UserGroupStore',"framework.TreeStore"],
    views: ['framework.user.View', "framework.user.MemberAddEdit","framework.util.TreeComboBox","framework.util.ActionColumnText"],
    // 选择器配置项
    refs: [
        // 用户主视图
        { ref: "userview", selector: "userview" },
        // 用户列表
        { ref: "memberList", selector: "memberlist" },
        // 用户tab panel
        { ref: "memberTabPanel", selector: "membertabpanel" },
        { ref: "addMember", selector: "#memberaddbutton" },
        { ref: "editMember", selector: "#membereditbutton" },
        { ref: "delMember", selector: "#memberdelbutton" }
    ],
    openedPanels: Ext.create("Ext.util.MixedCollection"),
    init: function () {
        var me = this;
        me.control({
            'memberlist': {
                // 视图准备好之后，才去请求GridPanel的数据。
                viewready: function () {
                    var store =Ext.StoreManager.lookup("framework.user.UserStore");
                    store.getProxy().extraParams = {};
                    store.load();
                },
                //视图加载前 添加操作
                beforerender: function (panel) {
//                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
//                    if (btns != null) {
//                        for (var i = 0; i < btns.length; i++) {
//
//                            panel.down("toolbar").add(btns[i]);
//                            if (i == 0) {
//                                panel.down("toolbar").add("->");
//                            }
//                        }
//                    }
                    var btns = Wms.getOperates.fn(Wms.menuInfo.operates);
//                    var btn = {};
                    var columns=[];
                    for(var i=0;i<panel.columns.length;i++){
                        columns.push(panel.columns[i].initialConfig);
                    }
                    if (btns != null) {
                        for (var i = 0; i < btns.length; i++) {

//                            btn = {
//                                text: btns[i].text,
//                                disabled: btns[i].disabled,
//                                action: btns[i].action
//                            };


                            if(btns[i].text=="重置"){
                                columns.push(
//                                    {
//                                        xtype: 'actioncolumntext',
//                                        sortable: false,
//                                        menuDisabled: true,
//
//                                        text:"操作",
//                                        menuText: '<i>操作</i>',
//                                        align:"center",
//                                        dataIndex:"status",
//                                        renderer:function(value){
//                                            if(value==1){
//                                                this.items[0].text="解冻"
//                                            }else{
//                                                this.items[0].text="冻结"
//                                            }
//                                        },
//                                        items: [
//                                            {  xtype:"button", text:"正常/解冻", action:"oneditMember",
//                                                handler: function (grid, rowIndex, colIndex, node, e, record, rowEl) {
//                                                    this.fireEvent('itemclick', this, grid, rowIndex, colIndex, node, e, record, rowEl);
//                                                }
//                                            }]
//                                    },
                                    {
                                        xtype: 'actioncolumntext',
                                        sortable: false,
                                        menuDisabled: true,
                                        text:"密码 ",
                                        menuText: '<i>密码 </i>',
                                        align:"center",
                                        items: [
                                            {   xtype:"button",
                                                text:"重置",
                                                action:"initMember",
                                                handler: function (grid, rowIndex, colIndex, node, e, record, rowEl) {
                                                    this.fireEvent('itemclick', this, grid, rowIndex, colIndex, node, e, record, rowEl);
                                                }

                                            }
                                        ]
                                    });
                            }else{
                                panel.down("toolbar").add(btns[i]);
                            }
                            if (i == 0) {
                                panel.down("toolbar").add("->");
                            }
                        }
                    }
                    panel.reconfigure(panel.store,columns);
                }
            },
            'userview [action=addMember]': {
                click: me.onShowAddPanel
            },
            'userview [action=editMember]': {
                click: me.onShowEditPanel
            },
            'userview [action=delMember]': {
                click: me.onDelMember
            },
            'memberlist actioncolumntext': {
                itemclick: me.buttonActionController
            }
        });

    },

    //控制操作列执行分支
    buttonActionController: function (t, grid, rowIndex, colIndex, node, e, record, rowEl) {
        var me = this;
        var action = node.action;
        switch (action) {
            case "initMember":
                me.onInitMember(t, grid, rowIndex, colIndex, node, e, record, rowEl);
                break;
            default :
                alert("未处理");
                break;
        }
    },
    onLaunch: function () {
       // console.log("MemberController.onLaunch()------------------------------------------------------------------------------------")
    },
    // 添加功能
    onShowAddPanel: function (btn) {
        var me = this;
        var panelName, panel;
        if (btn.hasOwnProperty("action")) {
            panelName = btn.action;
            // 尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);
            if (panel) {
                // 面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {
                // 面板不存在，创建
                panel = Ext.widget("memberaddedit", {
                    title: "添加用户",
                    closable: true, // 可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto" // 自动添加滚动条
                });
                
                // 显示面板，将面板添加入打开的面板集合中.
                me.getMemberTabPanel().add(panel).show();
                me.openedPanels.add(panelName, panel);
                var userListModel = Ext.create("Wms.model.framework.user.UserListModel");
                var form = panel.getForm();
                form.loadRecord(userListModel);
                // 提交按钮事件
                panel.down("button[action=memberSubmit]").on("click",
                    function () {
                        if (form.isValid() && form.isDirty()) {
                            Ext.Ajax.request({
                                url: "member/save.json",
                                params: {
                                    userName: Ext.getCmp('userName')
                                        .getValue(),
                                    password: Ext.getCmp('password')
                                        .getValue(),
                                    email: Ext.getCmp('email').getValue(),
                                    'group.groupId': Ext.getCmp('group').getValue(),
                                    'role.roleId': Ext.getCmp('role').getValue()

                                },
                                success: function (response, opts) {
                                    var obj = Ext
                                        .decode(response.responseText);
                                    if (obj.success) {
                                        Ext.StoreManager.lookup("framework.user.UserStore").load();
                                        Ext.Msg.alert("成功", obj.message);
                                        Ext.getCmp('memberAddEditForm').close();
                                        Wms.refresh.fn();//刷新桌面
                                    } else {
                                        Ext.Msg.alert("错误", obj.message)
                                    }
                                },
                                failure: function (response, opts) {
                                    Ext.Msg
                                        .alert(
                                            "错误",
                                            '状态：'
                                                + response.status
                                                + ":"
                                                + response.responseText);
                                }
                            });
                        }

                    });
                //重置按钮
                panel.down("button[action=memberReset]").on("click", function () {
                    form.reset();
                });
                // 关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });

            }
        }
    },

    // 编辑功能
    onShowEditPanel: function (btn) {
        var me = this;
        var panelName, panel;
        if (btn.hasOwnProperty("action")) {
            panelName = btn.action;
            var rec = this.getMemberList().getSelectionModel().getSelection();
            if (rec.length <= 0) {
                return;
            }
            panelName = panelName + rec[0].data["id"];
            // 尝试在集合中获取面板
            panel = me.openedPanels.get(panelName);
            if (panel) {
                // 面板存在，切换
                panel.isHidden() ? panel.show() : null;
            } else {
                // 面板不存在，创建
                // 添加用户面板
                panel = Ext.widget("memberaddedit", {
                    title: "编辑" + rec[0].data["userName"],
                    closable: true, // 可关闭。
                    iconCls: 'tabs',
                    overflowY: "auto", // 自动添加滚动条
                    flag:true,
                    roleId:rec[0].data["role.roleId"],
                    groupId:rec[0].data["group.groupId"]
                });
                // 显示面板，将面板添加入打开的面板集合中.
                me.getMemberTabPanel().add(panel).show();
                me.openedPanels.add(panelName, panel);
                // 获取到表单对象
                var form = panel.getForm();
                form.loadRecord(rec[0]);
                // 提交按钮事件
                panel.down("button[action=memberSubmit]").on("click",
                    function () {
                        if (form.isValid() && form.isDirty()) {
                            Ext.Ajax.request({
                                url: "member/update.json",
                                params: {
                                    uid: Ext.getCmp('uid').getValue(),
                                    userName: Ext.getCmp('userName')
                                        .getValue(),
                                    email: Ext.getCmp('email').getValue(),
                                    'role.roleId': Ext.getCmp('role').getValue(),
                                    'group.groupId': Ext.getCmp('group').getValue()
                                },
                                success: function (response, opts) {
                                    var obj = Ext
                                        .decode(response.responseText);
                                    if (obj.success) {
                                        Ext.Msg.alert("成功", obj.message)
                                        //me.get_UserListStoreStore().load();
                                        Ext.StoreManager.lookup("framework.user.UserStore").load();
                                        Ext.getCmp('memberAddEditForm').close();
                                        Wms.refresh.fn();//刷新桌面
                                    } else {
                                        Ext.Msg.alert("错误", obj.message)
                                    }
                                },
                                failure: function (response, opts) {
                                    Ext.Msg
                                        .alert(
                                            "错误",
                                            '状态：'
                                                + response.status
                                                + ":"
                                                + response.responseText);
                                }
                            });
                        }
                    });
                // 重置按钮
                panel.down("button[action=memberReset]").on("click",
                    function () {
                        form.reset();
                    });
                // 关闭面板时，在集合中除名
                panel.on({
                    destroy: function (p) {
                        me.openedPanels.remove(p);
                    }
                });

            }
        }
    },
    // 删除功能
    onDelMember: function () {
        //console.log("onDelMember");
        var me = this;
        var rec = this.getMemberList().getSelectionModel().getSelection();
        if (rec.length <= 0) {
            Ext.MessageBox.alert({
                title: '错误提示',
                msg: '请选择要删除的项',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.INFO
            });
            return;
        }
        // 提示是否删除
        var msg = ["确定删除以下用户吗？"];
        var ids = [];
        for (var i = 0, len = rec.length; i < len; i++) {
            ids.push(rec[i].data["uid"]);
            msg.push(rec[i].data["userName"]);
        }
        Ext.Msg.confirm("删除用户", msg.join("<br />"), function (btn) {
            if (btn === "yes") {
                Ext.Ajax.request({
                    url: "member/delete.json",
                    params: {
                        listCheckbox: ids
                    },
                    success: function (response, opts) {
                        var obj = Ext
                            .decode(response.responseText);
                        if (obj.success) {
                            Ext.Msg.alert("删除成功", "删除成功",
                                function () {
                                    //me.get_UserListStoreStore().load();
                                    Ext.StoreManager.lookup("framework.user.UserStore").loadPage(1);
                                })
                        } else {
                            Ext.Msg.alert("错误", obj.msg)
                        }
                    },
                    failure: function (response, opts) {
                        Ext.Msg
                            .alert(
                                "错误",
                                '状态：'
                                    + response.status
                                    + ":"
                                    + response.responseText);

                    }
                });

            }
        });
    },
    //重置用户密码
    onInitMember: function (t, grid, rowIndex, colIndex, node, e, record, rowEl) {
        var userId = record.data["uid"];
        var userName = record.data['userName'];



        // 提示是否删除
        var msg = ["确定重置用户["+userName+"]的密码？"];
        Ext.Msg.confirm("密码重置", msg.join("<br />"), function (btn) {
            if (btn === "yes") {
                Ext.Ajax.request({
                    url: "member/initPwd.json",
                    params: {
                        uid: userId
                    },
                    success: function (response, opts) {
                        var obj = Ext.decode(response.responseText);
                        if (obj.success) {
                            Ext.Msg.alert("操作成功", "密码已被重置为“111111”",
                                function () {
                                    Ext.StoreManager.lookup("framework.user.UserStore").loadPage(1);
                                })
                        } else {
                            Ext.Msg.alert("错误", obj.msg)
                        }
                    },
                    failure: function (response, opts) {
                        Ext.Msg
                            .alert(
                                "错误",
                                '状态：'
                                    + response.status
                                    + ":"
                                    + response.responseText);

                    }
                });

            }
        });
    }
});
