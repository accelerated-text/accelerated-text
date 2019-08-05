import { getCellOptions }   from '../nlg-workspace/cell-options';

import Block                from './Block';
import { lime as color }    from './colors.sass';
import * as T               from './types';
import ValueIcon            from './icons/Value';


export default Block({

    type:                   'Cell',
    color,
    icon:                   ValueIcon({ color }),

    json: {
        colour:             color,
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
