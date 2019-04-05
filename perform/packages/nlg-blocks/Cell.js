import { getCellOptions }   from '../nlg-workspace/cell-options';
import { LIME }             from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';


export default Block({

    type:                   'Cell',

    json: {
        colour:             LIME,
        output:             T.STRING,
    },

    init() {

        this.appendDummyInput()
            .appendField(
                new Blockly.FieldDropdown(
                    () => getCellOptions( this.workspace )
                ),
                'name',
            )
            .appendField( 'cell' );
    },
});
