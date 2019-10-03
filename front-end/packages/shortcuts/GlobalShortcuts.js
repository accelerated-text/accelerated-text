import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import bottomBarContext     from '../bottom-bar/Context';
import composeContexts      from '../compose-contexts/';
import { focusWorkspace }   from '../blockly-helpers/';
import getKeyName           from '../get-key-name/';
import modalContext         from '../modal/Context';
import { withClient }       from '../graphql/';
import workspaceContext     from '../workspace-context/WorkspaceContext';

import { globalKeys }       from './key-map';


export default withClient( composeContexts({
    bottomBarContext,
    modalContext,
    workspaceContext,
})( class GlobalShortcuts extends Component {

    static propTypes = {
        bottomBarContext:   PropTypes.object.isRequired,
        client:             PropTypes.object.isRequired,
        modalContext:       PropTypes.object.isRequired,
        workspaceContext:   PropTypes.object.isRequired,
    };

    onKeyDown = evt => {
        const fn =          globalKeys[ getKeyName( evt )];
        if( fn ) {
            const {
                bottomBarContext,
                client,
                modalContext,
                workspaceContext: {
                    withWorkspace,
                },
            } = this.props;
            withWorkspace(( workspace, Blockly ) =>
                fn({
                    Blockly,
                    bottomBarContext,
                    graphqlClient:  client,
                    modalContext,
                    workspace,
                })
            );
        }
    };

    componentDidMount() {
        this.props.workspaceContext.onWorkspace( focusWorkspace );
    }

    render = props =>
        <div
            { ...props }
            onKeyDown={ this.onKeyDown }
            tabindex="0"
        />;
}));
