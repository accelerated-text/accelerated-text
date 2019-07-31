import classnames           from 'classnames';
import { h, Component }     from 'preact';

import ValueIcon            from '../block-icons/Value';
import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import addBlock             from './add-block';
import S                    from './Item.sass';


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
                className={ classnames( S.className, className ) }
                onClick={ this.onClick }
                onKeyDown={ this.onKeyDown }
                tabindex="0"
            >
                <div className={ S.icon }>
                    <ValueIcon color={ S.dictionaryItemColor } />
                </div>
                <div className={ S.nextIcon }>
                    <ValueIcon color={ S.modifierColor } />
                </div>
                <div className={ S.text }>{ text }</div>
            </li>
        );
    }
}
