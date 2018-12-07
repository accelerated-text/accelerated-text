import { h, mount }         from 'dom-dom';
import { times }            from 'ramda';

import Block                from './Block';


export default Block({

    type:                   'sentence',

    skipToolbox:            true,

    json: {
        colour:             300,
        previousStatement:  'Action',
        nextStatement:      'Action',
        message0:           'Sentence',
    },

    mutationToDom() {
        return mount(
            <mutation children_count={ this.inputList.length - 1 } />
        );
    },

    domToMutation( xmlElement ) {

        times(
            i => this.appendInput_( Blockly.INPUT_VALUE, `CHILD${ i }` ),
            xmlElement.getAttribute( 'children_count' )
        );
    },
});
