import { h, mount }         from 'dom-dom';

import {
    appendLabeledValue,
}                           from '../blockly-helpers/';
import { RED }              from '../styles/blockly-colors';

import Block                from './Block';
import * as T               from './types';
import toNlgJson            from './to-nlg-json';


export default Block({
    type:                   'AMR',

    json: {
        colour:             RED,
        output:             T.STRING,
    },

    domToMutation( xmlElement ) {

        this.conceptLabel =     xmlElement.getAttribute( 'concept_label' );

        this.appendDummyInput( 'concept_label' )
            .insertFieldAt( 0, this.conceptLabel );

        appendLabeledValue( this, 'dictionaryItem', 'lexicon' );

        this.roles =        JSON.parse( xmlElement.getAttribute( 'roles' ));
        this.roles.forEach( role =>
            appendLabeledValue( this, role.id, role.fieldLabel )
        );
    },

    mutationToDom() {

        return mount(
            <mutation
                concept_label={ this.conceptLabel }
                roles={ JSON.stringify( this.roles ) }
            />
        );
    },

    toNlgJson() {

        return toNlgJson( this );
    },
});
