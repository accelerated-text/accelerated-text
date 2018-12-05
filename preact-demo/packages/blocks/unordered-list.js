import Block                from './Block';


export default Block({

    type:                   'unordered-list',

    json: {
        colour:             180,
        previousStatement:  'Action',
        nextStatement:      'Action',
        message0:           'UL',
        message1:           '%1',
        args1: [{
            type:           'input_value',
            name:           'CHILD1',
        }],
    },

    input_count:            1,

    init() {

        this.setOnChange( e => {
            this.onChange();
        });
    },

    mutationToDom() {

        const el =      document.createElement( 'mutation' );
        el.setAttribute( 'input_count', this.input_count );
        return el;
    },


    domToMutation( xmlElement ) {

        const input_count =  parseInt(
            xmlElement.getAttribute( 'input_count' ),
            10
        );

        this.onChange( input_count );
    },

    appendNextInput() {

        this.input_count += 1;
        this.appendInput_( Blockly.INPUT_VALUE, `CHILD${ this.input_count }` );
    },

    onChange( input_count = 0 ) {

        const emptyInputs = this.inputList.filter( input => (
            input.isVisible() &&
                !input.connection || !input.connection.isConnected()
        ));

        emptyInputs.shift();

        if( emptyInputs.length < 1 ) {
            this.appendNextInput();
        } else if( emptyInputs.length > 1 ) {
            emptyInputs.pop();
            emptyInputs.forEach(
                input => this.removeInput( input.name )
            );
        }

        while( this.input_count < input_count ) {
            this.appendNextInput();
        }
    },
});
