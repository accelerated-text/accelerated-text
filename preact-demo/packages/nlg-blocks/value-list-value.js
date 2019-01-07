import { h, mount }         from 'dom-dom';

import Block                from './Block';


const PREFIX =              'value_';


export default Block({

    type:                   'value-list-value',

    json: {
        colour:             0,
        output:             null,
        message0:           '',
    },

    value_count:            0,

    init() {

        this.setOnChange( e => {
            this.onChange();
        });
        this.onChange();
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

        this.appendInput_( Blockly.INPUT_VALUE, `${ PREFIX }${ this.value_count }` );
        this.value_count += 1;
    },

    onChange() {

        const valueInputs = this.inputList.filter( input =>
            input.name && input.name.startsWith( PREFIX )
        );

        if( valueInputs.length === 0 ) {
            /// Always at least 2 inputs:
            this.appendNextInput();
            this.appendNextInput();
        } else if( valueInputs.length === 1 ) {
            /// Always at least 2 inputs:
            this.appendNextInput();
        } else {

            const emptyInputs = valueInputs.filter( input => (
                !input.connection || !input.connection.isConnected()
            ));

            if( emptyInputs.length === 0 ) {
                /// Always show at least 1 empty input:
                this.appendNextInput();
            } else if( emptyInputs.length > 1 && valueInputs.length !== 2 ) {
                /// Leave only 1 empty input at the end:
                /// (unless exactly 2 inputs)
                emptyInputs
                    .slice( 0, 1 )
                    .forEach(
                        input => this.removeInput( input.name )
                    );
            }
        }
    },
});
