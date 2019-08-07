import shortkey             from 'shortkey';

import { getSiblings }      from '../blockly-helpers/';


export const onKeyDown = selected => shortkey({

    onArrowDown: () => {

        const siblings =    getSiblings( selected );
        const nextSibling = siblings[ 1 + siblings.indexOf( selected )];

        if( nextSibling ) {
            nextSibling.select();
        }
    },
    onArrowUp: () => {

        const siblings =    getSiblings( selected );
        const prevSibling = siblings[ -1 + siblings.indexOf( selected )];

        if( prevSibling ) {
            prevSibling.select();
        }
    },
    onArrowLeft: () => {

        const parent =      selected.getParent();
        if( parent ) {
            parent.select();
        }
    },
    onArrowRight: () => {

        const firstChild =  selected.getChildren( true )[ 0 ];
        if( firstChild ) {
            firstChild.select();
        }
    },
});


export default ( workspace, Blockly = window.Blockly ) => {
    const svg =             workspace.getParentSvg();

    svg.tabIndex =          '0';
    svg.focus();

    workspace.getTopBlocks()[0].select();

    svg.addEventListener( 'keydown', evt => {

        if( ! Blockly.selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            onKeyDown( Blockly.selected )( evt );
        }
    });
};
