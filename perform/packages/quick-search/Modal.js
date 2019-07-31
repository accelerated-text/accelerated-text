import { h, Component }     from 'preact';

import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import Query                from './Query';
import Results              from './Results';
import S                    from './Modal.sass';


export default class QuickSearchModal extends Component {

    static contextType =    WorkspaceContext;

    state = {
        query:              '',
    };

    onChangeQuery = query => {
        this.setState({ query });
    };

    onClose = () => {
        this.props.onClose();
        this.context.withWorkspace( workspace => {
            workspace.getParentSvg().focus();
        });
    };

    onClickModal = evt => {
        evt.stopPropagation();
    };

    onKeyDown = evt => {
        if( evt.key === 'Escape' ) {
            this.onClose();
        }
    };

    render( props, { query }) {
        return (
            <div
                className={ S.className }
                onClick={ this.onClose }
                onKeyDown={ this.onKeyDown }
                tabindex="0"
            >
                <div className={ S.modal } onClick={ this.onClickModal }>
                    <Query
                        autofocus
                        onChange={ this.onChangeQuery }
                        value={ query }
                    />
                    <Results
                        onSelect={ this.props.onClose }
                        query={ query }
                    />
                </div>
            </div>
        );
    }
}
