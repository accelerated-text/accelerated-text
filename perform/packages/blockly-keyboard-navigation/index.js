import shortkey             from 'shortkey';

import { getSiblings }      from '../blockly-helpers/';


export const onKeyDown = ( workspace, Blockly ) => shortkey({
    onArrowDown: () => {
        const { selected } =    Blockly;

        if( ! selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            const siblings =        selected ? getSiblings( selected ) : workspace.getTopBlocks();
            const nextSibling =     siblings[ 1 + siblings.indexOf( selected )];

            if( nextSibling ) {
                nextSibling.select();
            }
        }
    },
    onArrowUp: () => {
        const { selected } =    Blockly;

        if( ! selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            const siblings =        getSiblings( selected );
            const previousSibling = siblings[ -1 + siblings.indexOf( selected )];

            if( previousSibling ) {
                previousSibling.select();
            }
        }
    },
    onArrowLeft: () => {
        const { selected } =    Blockly;

        if( ! selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            const parent =      selected.getParent();
            if( parent ) {
                parent.select();
            }
        }
    },
    onArrowRight: () => {
        const { selected } =    Blockly;

        if( ! selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            const firstChild =  selected.getChildren( true )[ 0 ];
            if( firstChild ) {
                firstChild.select();
            }
        }
    },
});


export default ( workspace, Blockly = window.Blockly ) => {
    const svg =             workspace.getParentSvg();

    svg.tabIndex =          '0';
    svg.addEventListener( 'keydown', onKeyDown( workspace, Blockly ));
};
