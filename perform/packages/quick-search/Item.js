import { h, Component }     from 'preact';

import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import addBlock             from './add-block';


export default class QuickSearchItem extends Component {

    static contextType =    WorkspaceContext;

    onClick = () => {
        this.context.withWorkspace(
            addBlock( this.props.item )
        );
        this.props.onSelect && this.props.onSelect();
    };

    onKeyDown = evt => {
        if( evt.key === 'Enter' ) {
            this.onClick();
        }
    };

    render({ className, item: { text }}) {
        return (
            <li
                children={ text }
                className={ className }
                onClick={ this.onClick }
                onKeyDown={ this.onKeyDown }
                tabindex="0"
            />
        );
    }
}
