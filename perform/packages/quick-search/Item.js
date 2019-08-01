import classnames           from 'classnames';
import { h, Component }     from 'preact';

import ValueIcon            from '../block-icons/Value';

import S                    from './Item.sass';


export default class QuickSearchItem extends Component {

    onClick = () => {
        this.props.onSelect && this.props.onSelect( this.props.item );
    };

    render({ className, isActive, item: { text }}) {
        return (
            <button
                className={ classnames(
                    S.className,
                    isActive && S.isActive,
                    className,
                ) }
                onClick={ this.onClick }
            >
                <div className={ S.icon }>
                    <ValueIcon color={ S.dictionaryItemColor } />
                </div>
                <div className={ S.nextIcon }>
                    <ValueIcon color={ S.modifierColor } />
                </div>
                <div className={ S.text }>{ text }</div>
            </button>
        );
    }
}
