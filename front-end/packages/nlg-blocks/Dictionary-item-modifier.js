import Block                from './Block';
import DictionaryItem       from './Dictionary-item';
import ModifierIcon         from './icons/Modifier';
import { red as color }     from './colors.sass';
import * as T               from './types';


export default Block({

    ...DictionaryItem,

    type:                   'Dictionary-item-modifier',
    color,
    icon:                   ModifierIcon({ color }),

    json: {
        ...DictionaryItem.json,
        colour:             color,
        message0:           'Dict.: %1%2',
        args0: [
            ...DictionaryItem.json.args0,
            {
                type:       'input_value',
                name:       'child',
                check:      T.AMR_or_TEXT,
            },
        ],
    },
});
