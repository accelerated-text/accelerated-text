import { h, mount }         from 'dom-dom';

import Block                from './Block';
import { bluer as color }   from './colors.sass';
import * as T               from './types';
import toNlgJson            from './to-nlg-json';
import ValueIcon            from './icons/Value';


export default Block({

    type:                   'Dictionary-item',
    color,
    icon:                   ValueIcon({ color }),

    json: {
        colour:             color,
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
