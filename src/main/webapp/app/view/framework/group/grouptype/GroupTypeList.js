
//全部新闻数据Grid视图

Ext.define("Wms.view.framework.group.grouptype.GroupTypeList",{
    extend:"Ext.grid.Panel",
    alias:"widget.groupTypeList",


    //面板默认配置
    overflowY:"auto",
    store: "framework.role.GroupTypeListStore",

    initComponent:function(){
        var me=this,
        //选择模式
        selectCheckBox={
            selType : 'checkboxmodel'
        };
        me.tbar=[];
        //删除选择控制
        me.selModel=selectCheckBox;

        //表头，列表标题
        me.columns=[
          //  new Ext.grid.RowNumberer(),
            {
                text: "ID",
                width: 50,
                sortable: true,
                dataIndex: 'roleId'
            },
            {
                text: "机构类型",
                width: 160,
                sortable: true,
                dataIndex: 'roleName'
              //  action:'queryRole'
            },
            {
                text: "机构类型描述",
                width: 160,
                sortable: true,
                dataIndex: 'roleDsc'
            },
            {
                text: "创建者",
                width: 160,
                sortable: true,
                dataIndex: 'createUser'
            },
            {
                text: "创建日期",
                width: 150,
                sortable: true,
                dataIndex: 'createDate',
                renderer:function(value){
                    var date=new Date(value*1);
                    return Ext.Date.format(date,'Y-m-d H:i:s')
                }
            },
            {
                width: 50,
                hidden:true,
                dataIndex: 'parentRoleId'
            }
        ];
        me.bbar =new Ext.PagingToolbar({
            store: "framework.role.GroupTypeListStore",
            style:{
                border:"none"
            },
            displayInfo: true,
            emptyMsg: "无内容",
            //显示右下角信息
            displayInfo: true,
//            displayMsg: '当前记录 {0} -- {1} 条 共 {2} 条记录',
            emptyMsg: "没有数据显示",
            prevText: "上一页",
            nextText: "下一页",
            refreshText: "刷新",
            lastText: "最后页",
            firstText: "第一页",
            beforePageText: "当前页",
            afterPageText: "共{0}页"
    });

            //选中记录后，启用工具按钮。
        me.listeners={
            selectionchange:function(selType,records){
                var isSelect=!(records.length>0);
                if(this.down("toolbar button[action=delRole]") != undefined)
                	this.down("toolbar button[action=delRole]").setDisabled(isSelect);
                if(this.down("toolbar button[action=editRole]") != undefined)
                	this.down("toolbar button[action=editRole]").setDisabled(isSelect);
                
                if(this.down("toolbar button[action=addRole]") != undefined && !isSelect)
                	this.down("toolbar button[action=addRole]").setText("添加子类型");
                else
                	this.down("toolbar button[action=addRole]").setText("添加类型");
                //Ext.getCmp("querySubRoleButton").setDisabled(isSelect);
            }
        };




        me.callParent();
    }

});
