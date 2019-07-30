import { h }                from 'preact';
import renderToString       from 'preact-render-to-string';

import Block                from '../block-component/BlockComponent';
import DictionaryItem       from '../nlg-blocks/Dictionary-item';


export const getBlock = {
    Word:   word =>
        <Block
            type={ DictionaryItem.type }
            mutation={{
                id:         word.id,
                name:       word.text,
            }}
        />,
};


export default item => ( workspace, Blockly ) => {
    const { Xml } =         Blockly;
    const createBlock =     getBlock[ item.__typename ];

    if( createBlock ) {
        const box =         workspace.getBlocksBoundingBox();

        const block = workspace.getBlockById(
            Xml.domToWorkspace(
                Xml.textToDom(
                    renderToString(
                        <xml>{ createBlock( item ) }</xml>
                    ),
                ),
                workspace,
            )[ 0 ]
        );

        const selected = (
            Blockly.selected
            && Blockly.selected.workspace === workspace
            && Blockly.selected
        );
        const selectedParent =  selected && selected.getParent();

        const target = (
            ( selected && ! selected.allInputsFilled() && selected )
            || ( selectedParent && ! selectedParent.allInputsFilled() && selectedParent )
        );

        if( target ) {
            /// connect to selected block or its parent:
            const firstInput = target.inputList.find( input => (
                ! input.connection.isConnected()
                && input.connection.isConnectionAllowed( block.outputConnection )
            ));
            if( firstInput ) {
                firstInput.connection.connect( block.outputConnection );
            }
        } else {
            /// move to center bottom:
            const { x, y } =    block.getRelativeToSurfaceXY();
            block.moveBy(
                ( box.x + box.width / 2 - x - block.width )     || 0,
                ( box.y + box.height    - y - block.height )    || 0,
            );
        }

        block.select();
        workspace.centerOnBlock( block.id );
        workspace.getParentSvg().focus();
    }
};

