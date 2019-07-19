import Block                from '../nlg-blocks/Block';
import { RED }              from '../styles/blockly-colors';
import * as T               from '../nlg-blocks/types';

import getBlockType         from './get-block-type';


const FIELDS_OFFSET =       2;


const defineArg = ( fields, role, i ) =>
    Object.assign( fields, {
        [`message${ i + FIELDS_OFFSET }`]:  `${ role.fieldLabel } %1`,
        [`args${ i + FIELDS_OFFSET }`]: [{
            type:           'input_value',
            name:           role.id,
            check:          T.TEXT,     /// TODO: implement type check
        }],
    });

const defineBlock = concept =>
    Block({
        type:               getBlockType( concept ),
        json: {
            colour:         RED,
            output:         T.STRING,
            message0:       concept.label,
            message1:       'lexicon: %1',
            args1: [{
                type:       'input_value',
                name:       'dictionaryItem',
                check:      T.TEXT,
            }],
            ...concept.roles.reduce( defineArg, {}),
        },
    });

export default ( Blockly, concepts ) =>
    concepts
        .map( defineBlock )
        .forEach( fn => fn( Blockly ));
