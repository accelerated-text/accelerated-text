import { h }                from 'preact';

import {
    addPreactBlock,
    findAllowedInput,
}                           from '../blockly-helpers/';
import Block                from '../block-component/BlockComponent';
import DictionaryItem       from '../nlg-blocks/Dictionary-item';
import Quote                from '../nlg-blocks/Quote';


export const getBlock = {
    Quote:  ({ text }) =>
        <Block
            type={ Quote.type }
            fields={{ text }}
        />,
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
    const createBlock =     getBlock[ item.__typename ];

    if( createBlock ) {
        const box =         workspace.getBlocksBoundingBox();

        const block = addPreactBlock(
            workspace,
            createBlock( item ),
            Blockly,
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

