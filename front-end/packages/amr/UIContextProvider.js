import { h, Component }     from 'preact';

import composeContexts      from '../compose-contexts/';
import { focusWorkspace }   from '../blockly-helpers/';
import Help                 from '../help/Help';
import ModalContext         from '../modal/Context';
import withAutofocusDiv     from '../autofocus-div/withAutofocusDiv';
import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import Context              from './UIContext';


export default composeContexts({
    modal:                  ModalContext,
    workspace:              WorkspaceContext,
})( class OpenedDictionaryItemContextProvider extends Component {

    state = {
        dictionaryItemId:           null,

        closeAll: () =>
            this.setState({
                dictionaryItemId:   null,
            }, this.props.modal.closeModal ),

        closeDictionaryItem: () =>
            this.setState({
                dictionaryItemId:   null,
            }),

        openDictionaryItem: dictionaryItemId =>
            this.setState({
                dictionaryItemId,
            }),

        openHelp: () => {
            const url =     window.location.href;
            this.props.modal.openComponentModal(
                withAutofocusDiv( Help )
            );
            this.props.modal.onCloseModal(() => {
                if( window.location.href !== url ) {
                    window.history.replaceState( null, window.title, url );
                }
                this.props.workspace.withWorkspace( focusWorkspace );
            });
        },
    };

    render( props, state ) {
        return <Context.Provider
            { ...props }
            value={ state }
        />;
    }
});
