/*
 * Note: such complex implementation is needed, because Blockly fires
 * 'ui selected' and 'move' events before deleting a block.
 */
export default ({ Blockly, workspace }) => {
    const { Events } =      Blockly;

    let isSelected =        true;
    let lastParentId =      null;

    const saveParent = blockId => {
        const block =       workspace.getBlockById( blockId );
        const parent =      block && block.getParent();
        lastParentId =      parent && parent.id;
    };

    workspace.addChangeListener( evt => {

        const isSelectEvent = (
            evt.type === Events.UI
            && evt.element === 'selected'
        );
        const isSelectNone = (
            isSelectEvent
            && evt.oldValue
            && ! evt.newValue
        );
        const isMoveSelected = (
            isSelected
            && evt.type === Events.MOVE
        );

        if( isSelectNone ) {
            isSelected =        false;
        } else if( isSelectEvent ) {
            isSelected =        true;
            saveParent( evt.newValue );
        } else if( isMoveSelected ) {
            saveParent( evt.blockId );
        } else if( evt.type === Events.BLOCK_DELETE ) {
            if( Blockly.selected ) {
                /// do nothing
            } else if( lastParentId ) {
                workspace.getBlockById( lastParentId ).select();
                lastParentId =  null;
            } else {
                workspace.getTopBlocks()[0].select();
            }
            isSelected =        true;
        }
    });
};
