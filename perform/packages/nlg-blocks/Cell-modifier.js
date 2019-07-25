import { getCellOptions }   from '../nlg-workspace/cell-options';
import { YELLOW }           from '../styles/blockly-colors';

import Block                from './Block';
import Cell                 from './Cell';
import * as T               from './types';


export default Block({

    ...Cell,

    type:                   'Cell-modifier',

    json: {
        ...Cell.json,
        colour:             YELLOW,
        output:             T.LIST,
    },

    init() {

        this.appendValueInput( 'input' )
            .appendField(
                new Blockly.FieldDropdown(
                    () => getCellOptions( this.workspace )
                ),
                'name',
            )
            .appendField( 'cell' );
    },
});
