import { h, mount }         from 'dom-dom';

import {
    appendLabeledValue,
}                           from '../blockly-helpers/';
import { RED }              from '../styles/blockly-colors';

import Block                from './Block';
import toNlgJson            from './to-nlg-json';
import * as T               from './types';


export default Block({
    type:                   'AMR',

    json: {
        colour:             RED,
        output:             T.STRING,
    },

    domToMutation( xmlElement ) {

        this.concept_id =       xmlElement.getAttribute( 'concept_id' );
        this.concept_label =    xmlElement.getAttribute( 'concept_label' );
        this.roles =            JSON.parse( xmlElement.getAttribute( 'roles' ));

        this.appendDummyInput( 'concept_label' )
            .insertFieldAt( 0, this.concept_label );

        appendLabeledValue( this, 'dictionaryItem', 'lexicon', T.TEXT );

        this.roles.forEach( role =>
            appendLabeledValue( this, role.id, role.fieldLabel, role.fieldType )
        );
    },

    mutationToDom() {

        return mount(
            <mutation
                concept_id={ this.concept_id }
                concept_label={ this.concept_label }
                roles={ JSON.stringify( this.roles ) }
            />
        );
    },

    toNlgJson() {

        const json =        toNlgJson( this );

        return {
            type:           json.type,
            srcId:          json.srcId,
            conceptId:      this.concept_id,
            dictionaryItem: json.dictionaryItem,
            roles: this.roles.map( role => ({
                name:       role.id,
                children:   [ json[role.id] ],
            })),
        };
    },
});
