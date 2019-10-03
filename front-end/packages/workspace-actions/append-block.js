import {
    addPreactBlock,
    focusWorkspace,
    getAvailableStatementConnections,
    getAvailableValueConnections,
}                           from '../blockly-helpers/';
import createDependencies   from '../quick-search/create-dependencies';
import { Error, Loading }   from '../ui-messages/';
import { checkConnections } from '../type-checks';
import QuickSearch          from '../quick-search/QuickSearch';
import resultToBlock        from '../quick-search/result-to-block';

import memoizeByType        from './memoize-by-type';


const appendResult = ( workspace, Blockly, connections ) => searchResult => {

    const box =             workspace.getBlocksBoundingBox();

    const block = addPreactBlock(
        workspace,
        resultToBlock( searchResult ),
        Blockly,
    );
    const blockConnection = (
        block.outputConnection
        || block.previousConnection
    );
    const targetConnection = connections.find( connection => (
        ! connection.isConnected()
        && connection.isConnectionAllowed( blockConnection )
    ));

    if( targetConnection && targetConnection.connect ) {
        targetConnection.connect( blockConnection );
    } else {
        /// move to center bottom:
        const { x, y } =    block.getRelativeToSurfaceXY();
        block.moveBy(
            ( box.x + box.width / 2 - x - block.width )     || 0,
            ( box.y + box.height    - y - block.height )    || 0,
        );
        workspace.centerOnBlock( block.id );
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
    const parent =          selected && selected.getSurroundParent();
    const statementConnections = (
        selected
            ? getAvailableStatementConnections( selected )
            : []
    );
    const valueConnections = [
        ...( selected ? getAvailableValueConnections( selected ) : []),
        ...( parent ? getAvailableValueConnections( parent ) : []),
    ];

    const onClose =         () => focusWorkspace( workspace );
    openComponentModal( QuickSearch, {
        onChooseResult: result => {
            openComponentModal( Loading );
            onCloseModal( onClose );
            createDependencies( result, graphqlClient )
                .then( appendResult( workspace, Blockly, [
                    ...valueConnections,
                    ...statementConnections,
                ]))
                .then( closeModal )
                .catch( message => {
                    console.error( message );
                    openComponentModal( Error, { message });
                });
        },
        filterTypes: memoizeByType( type => (
            ! selected
                ? true
            : type.json.output !== undefined
                ? checkConnections( valueConnections, type.json.output )
            : type.json.previousStatement !== undefined
                ? checkConnections( statementConnections, type.json.previousStatement )
                : false
        )),
    });
    onCloseModal( onClose );
};
