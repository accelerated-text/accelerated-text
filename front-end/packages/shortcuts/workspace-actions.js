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
        const nextSibling = (
            selected.getNextBlock()
            || (
                siblings.includes( selected )
                && siblings[ 1 + siblings.indexOf( selected )]
            )
        );

        if( nextSibling ) {
            nextSibling.select();
        }
    },

    selectPreviousSibling: selected => {

        const parent =          selected.getSurroundParent();
        const previousBlock =   selected.getPreviousBlock();
        const siblings =        getSiblings( selected );
        const prevSibling = (
            ( previousBlock !== parent && previousBlock )
            || siblings[ -1 + siblings.indexOf( selected )]
        );

        if( prevSibling ) {
            prevSibling.select();
        }
    },

    selectParent: selected => {

        const parent =      selected.getSurroundParent();
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
