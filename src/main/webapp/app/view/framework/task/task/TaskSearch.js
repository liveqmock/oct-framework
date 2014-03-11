/**
 * 表单搜索
 */

Ext.define("Wms.view.framework.task.task.TaskSearch",{
    //组件类型。
    extend:"Ext.form.Panel",

    //设置组件引用名称
    alias:"widget.taskserach",

    //设置组件ID名，后面可使用Ext.getCmp("idName")快速获取的组件
   // id:"newsNavMenu",




    //搜索面板配置
    layout: 'form',
    url: '',
    bodyPadding: '5 5 0',

    fieldDefaults: {
        labelAlign: 'top',
        msgTarget: 'side'
    },

    autoScroll:true,

    //初始化组件，组件如果需要使用服务端的某些数据，应该写在这里。
    //如搜索表单中的combo需要加载服务器中的数据。
    initComponent:function(){

        this.items= [

            //按钮区域
            {
                xtype: 'container',
                layout: {
                    type:"hbox",
                    pack: 'center',
                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 5}
                },
                defaults: {
                    hideLabel: true,
                    height:24,
                    width:74
                },
                items: [
                    //提交按钮
                    {
                        xtype:"button",
                        text: '搜  索',
                        action:'taskSearch',
                        handler:function(){
                        }
                    },

                    //重置按钮
                    {
                        xtype:"button",
                        text:"重  置",
                        handler:function(){
                            this.up("form").getForm().reset();
                        }
                    }
                ]
            }
        ]

        this.callParent();
    }



})
