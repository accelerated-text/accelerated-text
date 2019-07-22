import { h, mount }         from 'dom-dom';

import { BLUER }            from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import toNlgJson            from './to-nlg-json';


export default Block({

    type:                   'Dictionary-item',

    json: {
        colour:             BLUER,
        output:             T.LIST,
        message0:           'Dict.: %1',
        args0: [{
            type:           'field_label',
            name:           'name',
            text:           '<ERROR: FIELD VALUE NOT SET!>',
        }],
    },

    domToMutation( xmlElement ) {

        this.itemId =       xmlElement.getAttribute( 'id' );
        this.itemName =     xmlElement.getAttribute( 'name' );

        this.getField( 'name' )
            .setValue( this.itemName );
    },

    mutationToDom() {
        return mount(
            <mutation
                id={ this.itemId }
                name={ this.itemName }
            />
        );
    },

    toNlgJson() {
        return {
            ...toNlgJson( this ),
            itemId:         this.itemId,
        };
    },
});
