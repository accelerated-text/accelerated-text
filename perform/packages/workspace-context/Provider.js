import { h, Component }     from 'preact';

import WorkspaceContext     from './WorkspaceContext';


export default class WorkspaceContextProvider extends Component {

    onBlocklyFns =                  [];
    onWorkspaceFns =                [];

    state = {
        Blockly:                    null,
        workspace:                  null,

        onBlockly: fn => {
            this.onBlocklyFns.push( fn );
            if( this.state.Blockly ) {
                fn( this.state.Blockly );
            }
        },

        onWorkspace: fn => {
            this.onWorkspaceFns.push( fn );
            if( this.state.workspace ) {
                fn( this.state.workspace, this.state.Blockly );
            }
        },

        setBlockly: Blockly => {
            this.setState({ Blockly });
            this.onBlocklyFns.forEach( fn =>
                fn( Blockly )
            );
        },

        setWorkspace: workspace => {
            this.setState({ workspace });
            if( workspace ) {
                this.onWorkspaceFns.forEach( fn =>
                    fn( workspace, this.state.Blockly )
                );
            }
        },

        withWorkspace: fn => {
            const { Blockly, workspace } =  this.state;
            if( workspace ) {
                fn( workspace, Blockly );
            }
        },
    };

    render = ({ children }) =>
        <WorkspaceContext.Provider
            children={ children }
            value={ this.state }
        />;
}
