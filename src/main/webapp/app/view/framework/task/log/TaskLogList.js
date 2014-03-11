
//全部分组数据Grid视图

Ext.define("Wms.view.framework.task.log.TaskLogList",{
    extend:"Ext.grid.Panel",
    alias:"widget.taskloglist",


    //面板默认配置
    overflowY:"auto",
    store: "framework.task.TaskLogStore",

    initComponent:function(){
        var me=this,
            //选择模式
            selectCheckBox={
                selType : 'checkboxmodel'
            };

		me.tbar=[];


        //根据登录用户类型userType设置可操作的button
        userType=Wms.curUser.roles;

        if(userType!=0){
      //      buttons=['->',btn2,btn3];
            selectCheckBox=null;
        }
        me.selModel=selectCheckBox;
        //表头，列表标题
        me.columns=[
          //  new Ext.grid.RowNumberer(),
            {
                width: 160,
                text: "日志ID",
                sortable: true,
                dataIndex: 'logId'
            },
            {
                width: 160,
                text: "日志级别",
                sortable: true,
                dataIndex: 'level'
            },
            {
                width: 160,
                text: "日志信息",
                sortable: true,
                dataIndex: 'description'
            },
            {
                text: "创建日期",
                width: 150,
                sortable: true,
                dataIndex: 'createTime',
                renderer:function(value){
                    var date=new Date(value*1);
                    return Ext.Date.format(date,'Y-m-d H:i:s')
                }
            }
        ];
        me.bbar={
            xtype: 'pagingtoolbar',
            displayInfo: true,
            store: "framework.task.TaskLogStore",
            emptyMsg: "无内容"
        };
        //选中记录后，启用工具按钮。
        me.listeners={
            selectionchange:function(selType,records){
                var isSelect=!(records.length>0);
                var status =records[0].data["status"];
                var pasueTasButton = this.down("toolbar button[action=pauseTask]");
                if( pasueTasButton!= undefined){
                    if(status==0){
                        pasueTasButton.setText("暂停");
                    }else{
                        pasueTasButton.setText("恢复");
                    }
                    pasueTasButton.setDisabled(isSelect);
                }
            }
        }

        me.callParent();
    }

})
