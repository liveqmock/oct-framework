
//全部分组数据Grid视图

Ext.define("Wms.view.framework.task.task.TaskList",{
    extend:"Ext.grid.Panel",
    alias:"widget.tasklist",


    //面板默认配置
    overflowY:"auto",
    store: "framework.task.TaskStore",

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
                text: "任务ID",
                sortable: true,
                dataIndex: 'taskId'
            },
            {
                width: 160,
                text: "组名称",
                sortable: true,
                dataIndex: 'groupName'
            },
            {
                width: 160,
                text: "任务名称",
                sortable: true,
                dataIndex: 'taskName'
            },
            {
                width: 160,
                text: "状态",
                sortable: true,
                dataIndex: 'status',
                renderer:function(value){
                    if(value==-1){ //STATE_NONE,触发器不存在
                        return "Trigger不存在";
                    }else if(value==0){ //STATE_NORMAL  正常
                        return "运行中";
                    }else if(value==1){ //STATE_PAUSED 暂停
                        return "已暂停";
                    }else if(value==2){//STATE_COMPLETE 完整的
                        return "完整的";
                    }else if(value==3){//STATE_ERROR  异常错误
                        return "错误";
                    }else if(value==4){//STATE_BLOCKED 阻塞
                        return "阻塞";
                    }

                }
            }
        ];
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
