import { h, mount }         from 'dom-dom';

import Block                from './Block';


export default Block({

    type:                   'unordered-list',

    json: {
        colour:             180,
        previousStatement:  'Action',
        nextStatement:      'Action',
        message0:           'all words',
    },

    input_count:            0,

    init() {

        this.setOnChange( e => {
            this.onChange();
        });
        this.onChange();
    },

    mutationToDom() {

        return mount(
            <mutation input_count={ this.input_count } />
        );
    },


    domToMutation( xmlElement ) {

        this.onChange(
            xmlElement.getAttribute( 'input_count' )
        );
    },

    appendNextInput() {

        this.appendInput_( Blockly.INPUT_VALUE, `CHILD${ this.input_count }` );
        this.input_count += 1;
    },

    onChange( input_count = 0 ) {

        const emptyInputs = this.inputList.filter( input => (
            input.name
            && ( !input.connection || !input.connection.isConnected())
        ));

        if( emptyInputs.length < 1 ) {
            this.appendNextInput();
        } else if( emptyInputs.length > 1 ) {
            emptyInputs
                .slice( 0, 1 )
                .forEach(
                    input => this.removeInput( input.name )
                );
        }

        while( this.input_count < input_count ) {
            this.appendNextInput();
        }
    },
});
