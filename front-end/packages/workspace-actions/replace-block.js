/* eslint-disable quote-props */

import { allPass }          from 'ramda';

import {
    addPreactBlock,
    focusWorkspace,
    getStatementTargets,
    getTarget,
    getValueTargets,
    replaceBlock,
}                           from '../blockly-helpers/';
import compareWith          from '../compare-with/';
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


const replaceWithResult = ( workspace, Blockly, oldBlock ) => searchResult => {

    const newBlock = replaceBlock(
        oldBlock,
        addPreactBlock(
            workspace,
            resultToBlock( searchResult ),
            Blockly,
        ),
    );

    /// remove old block if empty:
    if( ! oldBlock.getChildren().length && ! oldBlock.getNextBlock()) {
        oldBlock.dispose();
    }

    /// select new block
    newBlock.bumpNeighbours_(); // eslint-disable-line no-underscore-dangle
    newBlock.select();
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
    if( ! selected || ! selected.isDeletable()) {
        return;
    }

    const targets = {
        output:             getTarget( selected.outputConnection ),
        next:               getTarget( selected.nextConnection ),
        prev:               getTarget( selected.previousConnection ),
        statements:         getStatementTargets( selected ),
        values:             getValueTargets( selected ),
    };

    const onClose =         () => focusWorkspace( workspace );
    openComponentModal( QuickSearch, {
        onChooseResult: result => {
            openComponentModal( Loading );
            onCloseModal( onClose );
            createDependencies( result, graphqlClient )
                .then( replaceWithResult( workspace, Blockly, selected ))
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
                targets.prev && ( type =>
                    checkConnection( targets.prev, type.json.previousStatement )
                ),
            ].filter( Boolean )),
        ),
        sortTypes: compareWith([
            memoizeByType( type =>
                type.type === selected.type
            ),
            targets.next && memoizeByType( type =>
                checkConnection( targets.next, type.json.nextStatement )
            ),
            targets.statements.length && memoizeByType( type =>
                countAvailableStatements( targets.statements, type )
            ),
            targets.values.length && memoizeByType( type =>
                countAvailableValues( targets.values, type )
            ),
        ]),
    });
    onCloseModal( onClose );
};
