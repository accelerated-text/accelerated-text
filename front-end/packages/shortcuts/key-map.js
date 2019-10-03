import appendBlock          from '../workspace-actions/append-block';
import prependBlock         from '../workspace-actions/prepend-block';
import quickJumpAction      from '../quick-jump/keyboard-action';
import replaceBlock         from '../workspace-actions/replace-block';

import W                    from './workspace-actions';


export const globalKeys = {
    Ctrl_Alt_a:             appendBlock,
    'Ctrl_,':               appendBlock,

    Ctrl_Alt_r:             replaceBlock,
    'Ctrl_.':               replaceBlock,

    Ctrl_Alt_f:             quickJumpAction,
    'Ctrl_/':               quickJumpAction,

    Ctrl_Alt_p:             prependBlock,
    'Ctrl_Alt_,':           prependBlock,
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
