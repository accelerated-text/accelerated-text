import { h }                from 'preact';

import addBlock             from '../quick-search/add-block';
import { focusWorkspace }   from '../blockly-helpers/';
import QuickSearch          from '../quick-search/QuickSearch';


export default {
    appendToSelected: ( workspace, { Blockly, closeModal, onCloseModal, openComponentModal }) => {

        openComponentModal( QuickSearch, {
            onChooseResult: item => {
                addBlock( item )( workspace, Blockly );
                closeModal();
            },
        });
        onCloseModal(() =>
            focusWorkspace( workspace )
        );
    },

    replaceSelected: workspace => {
        console.log( 'replaceSelected', workspace );
        focusWorkspace( workspace );
    },

    quickJump: workspace => {
        console.log( 'quickJump', workspace );
        focusWorkspace( workspace );
    },
};
