import { getSiblings }      from '../blockly-helpers/';
import isModifierKey        from '../is-modifier-key/';


const onPressDown = selected => {
    const siblings =        getSiblings( selected );
    const nextSibling =     siblings[ 1 + siblings.indexOf( selected )];

    if( nextSibling ) {
        nextSibling.select();
    }
};

const onPressUp = selected => {
    const siblings =        getSiblings( selected );
    const previousSibling = siblings[ -1 + siblings.indexOf( selected )];

    if( previousSibling ) {
        previousSibling.select();
    }
};

const onPressLeft = selected => {
    const parent =          selected.getParent();

    if( parent ) {
        parent.select();
    }
};

const onPressRight = selected => {
    const firstChild =      selected.getChildren( true )[ 0 ];

    if( firstChild ) {
        firstChild.select();
    }
};


const keyHandlers = {
    ArrowLeft:              onPressLeft,
    ArrowDown:              onPressDown,
    ArrowRight:             onPressRight,
    ArrowUp:                onPressUp,
};


export const onKeyDown = ( workspace, Blockly ) => evt => {
    const { selected } =    Blockly;

    const shouldHandle = (
        keyHandlers[ evt.key ]
        && ! isModifierKey( evt )
    );

    if( ! shouldHandle ) {
        return;
    }

    if( ! selected ) {
        workspace.getTopBlocks()[0].select();
    } else {
        keyHandlers[ evt.key ]( selected, workspace );
    }
};


export default ( workspace, Blockly = window.Blockly ) => {
    const svg =             workspace.getParentSvg();

    svg.tabIndex =          '0';
    svg.addEventListener( 'keydown', onKeyDown( workspace, Blockly ));
};
