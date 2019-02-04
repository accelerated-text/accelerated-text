import { h, mount }         from 'dom-dom';

import {
    appendLabeledValue,
    connectNextBlock,
    createBlock,
    getAllNextBlocks,
    getInputsByPrefix,
    getValueInputs,
    orderInputs,
} from '../blockly-helpers';

import Block                from './Block';
import * as T               from './types';
import {
    blockToJson,
    fieldsToJson,
    statementsToJson,
}   from './to-nlg-json';


const ELSE_IF_LABEL =       'else if';
const ELSE_IF_PREFIX =      'else_if_';
const ELSE_NAME =           'else';
const IF_NAME =             'if';
const THEN_LABEL =          'then';
const THEN_NAME =           'then';
const THEN_PREFIX =         'then_';

const BT_IF =                'controls_if_if';
const BT_ELSE_IF =           'controls_if_elseif';


const getThenName = condName => (
    condName === ELSE_NAME
        ? ELSE_NAME
    : condName === IF_NAME
        ? THEN_NAME
    : condName.startsWith( ELSE_IF_PREFIX )
        ? `${ THEN_PREFIX }${ condName.replace( ELSE_IF_PREFIX, '' )}`
        : null
);


export default Block({

    type:                   'If-then-else',

    json: {
        colour:             164,
        output:             T.ANY,
        message0:           'if %1',
        args0: [{
            type:           'input_value',
            name:           IF_NAME,
            check:          T.ATOMIC_VALUE,
        }],
        message1:           'then %1',
        args1: [{
            type:           'input_value',
            name:           THEN_NAME,
            check:          T.ANY,
        }],
        message2:           'else %1',
        args2: [{
            type:           'input_value',
            name:           ELSE_NAME,
            check:          T.ANY,
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
                    appendLabeledValue( this, `${ ELSE_IF_PREFIX }${ n }`, ELSE_IF_LABEL )
                        .setCheck( T.ATOMIC_VALUE ),
                    appendLabeledValue( this, `${ THEN_PREFIX }${ n }`, THEN_LABEL )
                        .setCheck( T.ANY ),
                    this.getInput( ELSE_NAME ),
                ]);
            }
        }
        /// Update input count:
        this.else_if_count =    newCount;
        /// Fix orphan block visibility:
        this.bumpNeighbours_();
    },

    toNlgJson() {

        const getChildJson = inputName => {
            const block = this.getInputTargetBlock( inputName );
            return (
                block
                    ? block.toNlgJson()
                    : null
            );
        };

        const conditions =
            getValueInputs( this )
                .filter( input => (
                    input.name === 'if'
                    || input.name === ELSE_NAME
                    || input.name.startsWith( ELSE_IF_PREFIX )
                ))
                .filter( input => this.getInputTargetBlock( input.name ))
                .map( input => {
                    if( input.name === ELSE_NAME ) {
                        return {
                            type:           'Default-condition',
                            condition:      true,
                            thenExpression: getChildJson( getThenName( input.name )),
                        };
                    } else {
                        return {
                            type:           'If-condition',
                            condition:      getChildJson( input.name ),
                            thenExpression: getChildJson( getThenName( input.name )),
                        };
                    }
                });

        return {
            ...statementsToJson( this ),
            conditions,
            ...fieldsToJson( this ),
            ...blockToJson( this ),
        };
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
            parseInt(
                xmlElement.getAttribute( 'else_if_count' ),
                10,
            )
        );
    },

    mutationToDom() {

        return mount(
            <mutation else_if_count={ this.else_if_count } />
        );
    },
});
