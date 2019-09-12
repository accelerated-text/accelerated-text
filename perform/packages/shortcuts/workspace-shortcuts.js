import { focusWorkspace }   from '../blockly-helpers/';
import getKeyName           from '../get-key-name/';

import { workspaceKeys }    from './key-map';


export default ({ Blockly, workspace }) => {

    focusWorkspace( workspace );

    workspace.getTopBlocks()[0].select();

    workspace.getParentSvg().addEventListener( 'keydown', evt => {

        if( ! Blockly.selected ) {
            workspace.getTopBlocks()[0].select();
        } else {
            const fn =      workspaceKeys[ getKeyName( evt )];
            if( fn ) {
                fn( Blockly.selected, {
                    Blockly,
                    evt,
                    workspace,
                });
            }
        }
    });
};
