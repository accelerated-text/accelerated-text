import { getSiblings }      from '../blockly-helpers/';


export default {

    moveUp: selected =>
        console.log( 'moveUp', selected ),

    moveDown: selected =>
        console.log( 'moveDown', selected ),

    moveAfterParent: selected =>
        console.log( 'moveAfterParent', selected ),

    moveIntoSibling: selected =>
        console.log( 'moveIntoSibling', selected ),

    selectNextSibling: selected => {

        const siblings =    getSiblings( selected );
        const nextSibling = siblings[ 1 + siblings.indexOf( selected )];

        if( nextSibling ) {
            nextSibling.select();
        }
    },

    selectPreviousSibling: selected => {

        const siblings =    getSiblings( selected );
        const prevSibling = siblings[ -1 + siblings.indexOf( selected )];

        if( prevSibling ) {
            prevSibling.select();
        }
    },

    selectParent: selected => {

        const parent =      selected.getParent();
        if( parent ) {
            parent.select();
        }
    },

    selectFirstChild: selected => {

        const firstChild =  selected.getChildren( true )[ 0 ];
        if( firstChild ) {
            firstChild.select();
        }
    },
};
