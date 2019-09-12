import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import composeContexts      from '../compose-contexts/';
import { focusWorkspace }   from '../blockly-helpers/';
import getKeyName           from '../get-key-name/';
import modalContext         from '../modal/Context';
import workspaceContext     from '../workspace-context/WorkspaceContext';

import { globalKeys }       from './key-map';


export default composeContexts({
    modalContext,
    workspaceContext,
})( class GlobalShortcuts extends Component {

    static propTypes = {
        modalContext:       PropTypes.object.isRequired,
        workspaceContext:   PropTypes.object.isRequired,
    };

    onKeyDown = evt => {
        const fn =          globalKeys[ getKeyName( evt )];
        if( fn ) {
            const {
                workspaceContext: {
                    withWorkspace,
                },
                modalContext: {
                    closeModal,
                    onCloseModal,
                    openModal,
                    openComponentModal,
                },
            } = this.props;
            withWorkspace(( workspace, Blockly ) =>
                fn( workspace, {
                    Blockly,
                    closeModal,
                    onCloseModal,
                    openComponentModal,
                    openModal,
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
});
