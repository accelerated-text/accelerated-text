import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import WorkspaceContext     from '../workspace-context/WorkspaceContext';


export default class QuickSearchKeyboardProvider extends Component {

    static contextType =    WorkspaceContext;

    static propTypes = {
        openQuickSearch:    PropTypes.func.isRequired,
    };

    onKeyDown = evt => {
        if( evt.ctrlKey && evt.key === '.' ) {
            this.props.openQuickSearch();
        }
    };

    componentDidMount() {
        this.context.onWorkspace( workspace => {
            const svg =     workspace.getParentSvg();

            svg.tabIndex = '0'; /// Fix keyboard issues with workspace:
            svg.focus();
        });
    }

    render( props ) {
        return <div
            { ...props }
            onKeyDown={ this.onKeyDown }
            tabindex="0"
        />;
    }
}
