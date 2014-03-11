Ext.define('Ext.ux.grid.column.ActionButtonColumn', {

    extend: 'Ext.grid.column.Column',
    alias: ['widget.actionbuttoncolumn'],
    alternateClassName: 'Ext.grid.ActionButtonColumn',

    /**
     * @cfg {Function} handler A function called when the button is clicked.
     * The handler is passed the following parameters:<div class="mdetail-params"><ul>
     * <li><code>view</code> : TableView<div class="sub-desc">The owning TableView.</div></li>
     * <li><code>rowIndex</code> : Number<div class="sub-desc">The row index clicked on.</div></li>
     * <li><code>colIndex</code> : Number<div class="sub-desc">The column index clicked on.</div></li>
     * </ul></div>
     */

    /**
     * @cfg {Array} items An Array which may contain multiple button definitions, each element of which may contain:
     * <div class="mdetail-params"><ul>
     * <li><code>text</code> : String<div class="sub-desc">The button text to be used as innerHTML (html tags are accepted).</div></li>
     * <li><code>iconIndex</code> : String<div class="sub-desc">Optional, however either iconIndex or iconCls must be configured. Field name of the field of the grid store record that contains css class of the button to show. If configured, shown icons can vary depending of the value of this field.</div></li>
     * <li><code>hideIndex</code> : String<div class="sub-desc">Optional. Field name of the field of the grid store record that contains hide flag (falsie [null, '', 0, false, undefined] to show, anything else to hide).</div></li>
     * <li><code>handler</code> : Function<div class="sub-desc">A function called when the button is clicked.</div></li>
     * </ul></div>
     */
    header: '&#160;',

    sortable: false,

    constructor: function(config) {

        var me = this,
            cfg = Ext.apply({}, config),
            items = cfg.items || [me],
            l = items.length,
            i,
            item;

        // This is a Container. Delete the items config to be reinstated after construction.
        delete cfg.items;
        me.callParent([cfg]);

        // Items is an array property of ActionButtonColumns
        me.items = items;

        // Renderer closure iterates through items creating a button element for each and tagging with an identifying
        me.renderer = function(v, meta, rec, rowIndex, colIndex, store, view) {

            //  Allow a configured renderer to create initial value (And set the other values in the "metadata" argument!)
            v = Ext.isFunction(cfg.renderer) ? cfg.renderer.apply(this, arguments)||'' : '';

            meta.tdCls += ' ' + Ext.baseCSSPrefix + 'action-col-cell';

            for (i = 0; i < l; i++) {

                item = items[i];

                var nid = Ext.id();
                var cls = Ext.baseCSSPrefix + 'action-col-button ' + Ext.baseCSSPrefix + 'action-col-button-' + String(i);
                var iconCls = item.iconIndex ? rec.data[item.iconIndex] : (item.iconCls ? item.iconCls : '');
               // var fun = Ext.bind(item.handler, me, [view, rowIndex, colIndex,rec,store]);
                var fun = Ext.bind(item.handler, me, [view, meta, rec, rowIndex, colIndex, store]);
                var hide = rec.data[item.hideIndex];
                Ext.Function.defer(me.createGridButton, 100, me, [item.text, nid, rec, cls, fun, hide, iconCls]);

                v += '<div id="' + nid + '">&#160;</div>';
            }
            return v;
        };
    },

    createGridButton: function(value, id, record, cls, fn, hide, iconCls) {
        new Ext.Button({
            text: value,
            cls: cls,
            iconCls: iconCls,
            hidden: hide,
            handler: fn
        }).render(Ext.getBody(), id);
        Ext.get(id).remove();
    },

    destroy: function() {
        delete this.items;
        delete this.renderer;
        return this.callParent(arguments);
    },

    cascade: function(fn, scope) {
        fn.call(scope||this, this);
    },

    // Private override because this cannot function as a Container, and it has an items property which is an Array, NOT a MixedCollection.
    getRefItems: function() {
        return [];
    }
});