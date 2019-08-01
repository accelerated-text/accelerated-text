import { h, Component }     from 'preact';

import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import Query                from './Query';
import Results              from './Results';


export default class QuickSearchModal extends Component {

    static contextType =    WorkspaceContext;

    state = {
        query:              '',
    };

    onChangeQuery = query => {
        this.setState({ query });
    };

    componentWillUnmount() {
        this.context.withWorkspace( workspace => {
            workspace.getParentSvg().focus();
        });
    }

    render = ({ onClose }, { query }) =>
        <div>
            <Query
                autofocus
                onChange={ this.onChangeQuery }
                value={ query }
            />
            <Results
                onSelect={ onClose }
                query={ query }
            />
        </div>;
}
