import { h, mount }         from 'dom-dom';

import {
    appendLabeledValue,
}                           from '../blockly-helpers/';

import Block                from './Block';
import { magenta as color } from './colors.sass';
import * as T               from './types';
import toNlgJson            from './to-nlg-json';
import TwoInputs            from './icons/TwoInputs';


export default Block({

    type:                   'AMR',
    color,
    icon:                   TwoInputs({ color }),

    json: {
        colour:             color,
        output:             T.STRING,
        message0:           '%1',
        args0: [{
            type:           'field_label',
            name:           'conceptLabel',
        }],
        message1:           'lexicon %1',
        args1: [{
            type:           'input_value',
            name:           'dictionaryItem',
            check:          T.TEXT,
        }],
    },

    domToMutation( xmlElement ) {

        this.conceptId =        xmlElement.getAttribute( 'concept_id' );
        this.conceptLabel =     xmlElement.getAttribute( 'concept_label' );
        this.roles =            JSON.parse( xmlElement.getAttribute( 'roles' ));

        this.getField( 'conceptLabel' )
            .setValue( this.conceptLabel );

        this.roles.forEach( role =>
            appendLabeledValue( this, role.id, role.fieldLabel, role.fieldType )
        );
    },

    mutationToDom() {
        return mount(
            <mutation
                concept_id={ this.conceptId }
                concept_label={ this.conceptLabel }
                roles={ JSON.stringify( this.roles ) }
            />
        );
    },

    toNlgJson() {

        const json =            toNlgJson( this );

        return {
            type:               json.type,
            srcId:              json.srcId,
            conceptId:          this.conceptId,
            dictionaryItem:     json.dictionaryItem,
            roles: this.roles.map( role => ({
                name:           role.id,
                children:       [ json[role.id] ],
            })),
        };
    },
});
