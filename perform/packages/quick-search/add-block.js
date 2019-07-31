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


export const findAllowedInput = ( block, target ) => (
    target
    && ! target.allInputsFilled()
    && target.inputList.find( input => (
        input.connection
        && ! input.connection.isConnected()
        && input.connection.isConnectionAllowed( block.outputConnection )
    ))
);


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

        const targetInput = (
            findAllowedInput( block, selected )
            || ( selectedParent && findAllowedInput( block, selectedParent ))
        );

        if( targetInput ) {
            targetInput.connection.connect( block.outputConnection );
            block.select();
        } else {
            /// move to center bottom:
            const { x, y } =    block.getRelativeToSurfaceXY();
            block.moveBy(
                ( box.x + box.width / 2 - x - block.width )     || 0,
                ( box.y + box.height    - y - block.height )    || 0,
            );
            block.select();
            workspace.centerOnBlock( block.id );
        }

        workspace.getParentSvg().focus();
    }
};

