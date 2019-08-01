import { h, Component }     from 'preact';

import Modal                from '../modal/Modal';
import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import addBlock             from './add-block';
import QuickSearch          from './QuickSearch';


export default class QuickSearchWorkspaceModal extends Component {

    static contextType =    WorkspaceContext;

    onClose = () => {
        this.props.onClose();
        this.context.withWorkspace( workspace => {
            workspace.getParentSvg().focus();
        });
    };

    onChooseResult = item => {
        this.context.withWorkspace( addBlock( item ));
        this.onClose();
    };

    render = () =>
        <Modal onClose={ this.onClose }>
            <QuickSearch onChooseResult={ this.onChooseResult } />
        </Modal>;
}
