//User数据
Ext.define("Wms.store.framework.task.TaskStore", {
    extend:'Ext.data.Store',
    model:'Wms.model.framework.task.TaskModel',
    //不允许批量操作
    batchActions: false,
    pageSize:15,
    proxy: {
        type:"ajax",

        api:{
           //获取角色数据请求地址
            read: 'task/triggerList.json',
            //更新任务状态
            updateTriggerStatus:'task/triggerStatus.json'
        },

        reader: {

            type: 'json',

            //固定数据返回项为  { success:true, Msg:"some text" , data:[   {...},......]  }
            root: "dataList",

            messageProperty: "message",
            totalProperty:"recordCount"

        },

        writer: {

            type: "json",

            //以键值对的方式提交，而不是json对象方式提交数据
            encode: true,

            root: "data",

            //不允许，一个个单独提交
            allowSingle: false

        }

    }

})


