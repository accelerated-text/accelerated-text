/* eslint-disable quote-props */

import { allPass }          from 'ramda';

import {
    addPreactBlock,
    findAllowedChildConnection,
    focusWorkspace,
    getTarget,
}                           from '../blockly-helpers/';
import createDependencies   from '../quick-search/create-dependencies';
import { Error, Loading }   from '../ui-messages/';
import {
    checkConnection,
    countAvailableStatements,
    countAvailableValues,
}                           from '../type-checks';
import QuickSearch          from '../quick-search/QuickSearch';
import resultToBlock        from '../quick-search/result-to-block';

import memoizeByType        from './memoize-by-type';


const prependResult = ( workspace, Blockly, targets ) => searchResult => {

    const block = addPreactBlock(
        workspace,
        resultToBlock( searchResult ),
        Blockly,
    );

    /// connect to parent:
    if( targets.output ) {
        block.outputConnection.connect( targets.output );
    } else if( targets.prev ) {
        block.previousConnection.connect( targets.prev );
    }

    /// connect to child (if needed):
    const childOutput =     targets.values[0];
    if( childOutput && ! childOutput.isConnected()) {
        childOutput.connect(
            findAllowedChildConnection( block, childOutput )
            || null
        );
    }

    block.select();
};


export default ({
    Blockly,
    graphqlClient,
    modalContext: {
        closeModal,
        onCloseModal,
        openComponentModal,
    },
    workspace,
}) => {
    const { selected } =    Blockly;
    if( ! selected ) {
        return;
    }

    const targets = {
        output:             getTarget( selected.outputConnection ),
        next:               selected.previousConnection,
        prev:               getTarget( selected.previousConnection ),
        values:             [ selected.outputConnection ].filter( Boolean ),
    };

    const onClose =         () => focusWorkspace( workspace );
    openComponentModal( QuickSearch, {
        onChooseResult: result => {
            openComponentModal( Loading );
            onCloseModal( onClose );
            createDependencies( result, graphqlClient )
                .then( prependResult( workspace, Blockly, targets ))
                .then( closeModal )
                .catch( message => {
                    console.error( message );
                    openComponentModal( Error, { message });
                });
        },
        filterTypes: memoizeByType(
            allPass([
                targets.output && ( type =>
                    checkConnection( targets.output, type.json.output )
                ),
                targets.next && ( type =>
                    checkConnection( targets.next, type.json.nextStatement )
                    || countAvailableStatements([ targets.next ], type )
                ),
                targets.prev && ( type =>
                    checkConnection( targets.prev, type.json.previousStatement )
                ),
                targets.values.length && ( type =>
                    countAvailableValues( targets.values, type )
                ),
            ].filter( Boolean )),
        ),
    });
    onCloseModal( onClose );
};
