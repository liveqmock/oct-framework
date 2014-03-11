
  //任务对象模型
Ext.define('Wms.model.framework.task.TaskModel', {
    extend:'Ext.data.Model',
    fields: [
        "taskId",
        { name: "taskName", defaultValue:"" },
        { name: "status",type:"string", defaultValue:"" },
        {name: "groupName", defaultValue: "" }
    ],
    idProperty: "taskId"
});
