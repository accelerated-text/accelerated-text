import { createContext }    from 'preact';


const errorMsg = fnName =>
    `WorkspaceContext Provider failed to provide function ${ fnName }.`;


export default createContext({
    Blockly:                null,
    workspace:              null,

    onBlockly() {
        throw Error( errorMsg( 'onBlockly' ));
    },
    onWorkspace() {
        throw Error( errorMsg( 'onWorkspace' ));
    },
    setBlockly() {
        throw Error( errorMsg( 'setBlockly' ));
    },
    setWorkspace() {
        throw Error( errorMsg( 'setWorkspace' ));
    },
    withWorkspace() {
        throw Error( errorMsg( 'withWorkspace' ));
    },
});
