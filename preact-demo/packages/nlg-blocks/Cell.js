import { LIME }         from '../styles/blockly-colors';

import Block            from './Block';
import * as T           from './types';


export default Block({

    type:                   'Cell',

    json: {
        colour:             LIME,
        output:             T.STRING,
        message0:           '%1 cell',
        args0: [{
            type:           'field_dropdown',
            name:           'name',
            options: [
                [ 'Color',              'Color' ],
                [ 'Lacing',             'Lacing' ],
                [ 'Main feature',       'Main Feature' ],
                [ 'Make',               'Make' ],
                [ 'Material',           'Material' ],
                [ 'Name',               'Name' ],
                [ 'Product name',       'Product name' ],
                [ 'Style',              'Style' ],
                [ 'Secondary feature',  'Secondary feature' ],
                [ 'Title',              'Title' ],
            ],
        }],
    },
});
