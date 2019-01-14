import { h, mount }         from 'dom-dom';

import {
    appendLabeledValue,
    connectNextBlock,
    createBlock,
    getAllNextBlocks,
    getInputsByPrefix,
    orderInputs,
} from '../blockly-helpers';

import Block                from './Block';


const ELSE_IF_LABEL =       'else if';
const ELSE_IF_PREFIX =      'else_if_';
const ELSE_NAME =           'else';
const THEN_LABEL =          'then';
const THEN_PREFIX =         'then_';

const BT_IF =                'controls_if_if';
const BT_ELSE_IF =           'controls_if_elseif';


export default Block({

    type:                   'if-then-else',

    json: {
        colour:             164,
        output:             null,
        message0:           'if %1',
        args0: [{
            type:           'input_value',
            name:           'if',
        }],
        message1:           'then %1',
        args1: [{
            type:           'input_value',
            name:           'then',
        }],
        message2:           'else %1',
        args2: [{
            type:           'input_value',
            name:           ELSE_NAME,
        }],
    },

    else_if_count:          0,

    fixInputCount( newCount ) {

        if( this.else_if_count > newCount ) {
            /// Remove extra inputs:
            getInputsByPrefix( this, ELSE_IF_PREFIX )
                .slice( newCount )
                .forEach( input => this.removeInput( input.name ));
            getInputsByPrefix( this, THEN_PREFIX )
                .slice( newCount )
                .forEach( input => this.removeInput( input.name ));
        } else {
            /// Add missing inputs:
            for( let n = this.else_if_count; n < newCount; n += 1 ) {
                orderInputs( this, [
                    appendLabeledValue( this, `${ ELSE_IF_PREFIX }${ n }`, ELSE_IF_LABEL ),
                    appendLabeledValue( this, `${ THEN_PREFIX }${ n }`, THEN_LABEL ),
                    this.getInput( ELSE_NAME ),
                ]);
            }
        }
        /// Update input count:
        this.else_if_count =    newCount;
        /// Fix orphan block visibility:
        this.bumpNeighbours_();
    },

    mutatorBlockList: [ BT_ELSE_IF ],

    compose( topBlock ) {

        this.fixInputCount(
            getAllNextBlocks( topBlock )
                .filter( block => block.type === BT_ELSE_IF )
                .length
        );
    },

    decompose( workspace ) {

        const topBlock =        createBlock( BT_IF, workspace );

        /// Create block chain from target inputs:
        getInputsByPrefix( this, ELSE_IF_PREFIX )
            .reduce(
                prevBlock =>
                    connectNextBlock(
                        prevBlock,
                        createBlock( BT_ELSE_IF, workspace ),
                    ),
                topBlock,
            );

        return topBlock;
    },

    domToMutation( xmlElement ) {

        this.fixInputCount(
            xmlElement.getAttribute( 'else_if_count' )
        );
    },

    mutationToDom() {

        return mount(
            <mutation else_if_count={ this.else_if_count } />
        );
    },
});
