import G                    from './global-actions';
import W                    from './workspace-actions';


export const globalKeys = {
    'Ctrl_,':               G.appendToSelected,
    'Ctrl_.':               G.replaceSelected,
    'Ctrl_/':               G.quickJump,
};

export const workspaceKeys = {
    ArrowUp:                W.selectPreviousSibling,
    ArrowDown:              W.selectNextSibling,
    ArrowLeft:              W.selectParent,
    ArrowRight:             W.selectFirstChild,
    Ctrl_Alt_ArrowUp:       W.moveUp,
    Ctrl_Alt_ArrowDown:     W.moveDown,
    Ctrl_Alt_ArrowLeft:     W.moveAfterParent,
    Ctrl_Alt_ArrowRight:    W.moveIntoSibling,
    Ctrl_Shift_ArrowUp:     W.moveUp,
    Ctrl_Shift_ArrowDown:   W.moveDown,
    Ctrl_Shift_ArrowLeft:   W.moveAfterParent,
    Ctrl_Shift_ArrowRight:  W.moveIntoSibling,
};
