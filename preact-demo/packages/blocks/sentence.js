import Block                from './Block';


export default Block({

    type:                   'sentence',

    noToolbox:              true,

    json: {
        colour:             300,
        previousStatement:  'Action',
        nextStatement:      'Action',
        message0:           'Sentence',
    },

    init() {

        this.setEditable( false );
    },

    mutationToDom() {

        const el =      document.createElement( 'mutation' );
        el.setAttribute( 'children_count', this.inputList.length - 1 );
        return el;
    },


    domToMutation( xmlElement ) {

        const children_count =  parseInt(
            xmlElement.getAttribute( 'children_count' ),
            10
        );

        for( let i = 0; i < children_count; i += 1 ) {
            this.appendInput_(
                Blockly.INPUT_VALUE,
                `CHILD${ i }`,
            );
        }
    },
});
