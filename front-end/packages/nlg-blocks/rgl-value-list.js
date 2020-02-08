import { h, mount }         from 'dom-dom';
import { props, omit }    from 'ramda';

import Block                from './Block';
import {
    blockToJson,
    fieldsToJson,
    statementsToJson,
    valuesToJson,
}   from './to-nlg-json';


const PREFIX =              'value_';


export default Block({

    type:                   null,

    json: {
        colour:             0,
        inputsInline:       false,
        message0:           '',
    },

    value_count:            0,

    init() {

        this.setOnChange( e => {
            this.onChange();
        });
        this.onChange();
    },

    toNlgJson() {
        const valueMap =    valuesToJson( this );

        const prefixedValues =
            Object.keys( valueMap )
                .filter( name => name.startsWith( PREFIX ));

        return {
            ...statementsToJson( this ),
            children:   props( prefixedValues, valueMap ),
            ...omit( prefixedValues, valueMap ),
            ...fieldsToJson( this ),
            ...blockToJson( this ),
        };
    },

    mutationToDom() {

        return mount(
            <mutation value_count={ this.value_count } />
        );
    },

    domToMutation( xmlElement ) {

        const valueCount = xmlElement.getAttribute( 'value_count' );

        this.onChange();
        while( this.value_count < valueCount ) {
            this.appendNextInput();
        }
    },

    appendNextInput() {

        this
            .appendValueInput( `${ PREFIX }${ this.value_count }` )
            .setCheck( this.valueListCheck || null );

        this.value_count += 1;
    },

    onChange() {

        const valueInputs = this.inputList.filter( input =>
            input.name && input.name.startsWith( PREFIX )
        );

        if( valueInputs.length === 0 ) {
            this.appendNextInput();
        }
    },
});
