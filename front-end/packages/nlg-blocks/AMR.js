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
    },

    domToMutation( xmlElement ) {

        this.conceptId =        xmlElement.getAttribute( 'concept_id' );
        this.kind =             xmlElement.getAttribute( 'kind' );
        this.conceptLabel =     xmlElement.getAttribute( 'concept_label' );
        this.roles =            JSON.parse( xmlElement.getAttribute( 'roles' ));

        this.getField( 'conceptLabel' )
            .setValue( this.conceptLabel );

        this.setOutput( true, this.kind );

        this.roles.forEach( role =>
            appendLabeledValue( this, role.id, role.fieldLabel, role.fieldType )
        );
    },

    mutationToDom() {
        return mount(
            <mutation
                concept_id={ this.conceptId }
                kind={ this.kind }
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
            kind:               this.kind,
            roles: this.roles.map( role => ({
                name:           role.id,
                children:       [ json[role.id] ],
            })),
        };
    },
});
