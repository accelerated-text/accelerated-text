import { getCellOptions }   from '../nlg-workspace/cell-options';

import Block                from './Block';
import Cell                 from './Cell';
import ModifierIcon         from './icons/Modifier';
import { red as color }     from './colors.sass';
import * as T               from './types';


export default Block({

    ...Cell,
    color,
    icon:                   ModifierIcon({ color }),

    type:                   'Cell-modifier',

    json: {
        ...Cell.json,
        colour:             color,
        output:             T.LIST,
    },

    init() {

        this.appendValueInput( 'child' )
            .appendField(
                new Blockly.FieldDropdown(
                    () => getCellOptions( this.workspace )
                ),
                'name',
            )
            .appendField( 'cell' );
    },
});
