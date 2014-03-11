
  //任务日志对象模型
Ext.define('Wms.model.framework.task.TaskLogModel', {
    extend:'Ext.data.Model',
    fields: [
        "logId",
        { name: "description"},
        { name: "level"},
        {name: "createTime" }
    ],
    idProperty: "logId"
});
