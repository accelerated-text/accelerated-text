import classnames           from 'classnames';
import { h, Component }     from 'preact';

import ModifierIcon         from '../block-icons/Modifier';
import MultiInputIcon       from '../block-icons/Multi-input';
import ValueIcon            from '../block-icons/Value';

import S                    from './Item.sass';


const TYPES = [
    <ValueIcon color={ S.dictionaryItemColor } />,
    <ModifierIcon color={ S.modifierColor } />,
    <MultiInputIcon color={ S.amrColor } />,
];

const getType = ( isActive, subtype, n ) =>
    TYPES[
        isActive
            ? ((
                ( TYPES.length + subtype % TYPES.length )
                + n
            ) % TYPES.length )
            : n
    ];


export default class QuickSearchItem extends Component {

    onClick = () => {
        this.props.onSelect && this.props.onSelect( this.props.item );
    };

    render({ className, isActive, item: { text }, subtype }) {
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
                    { getType( isActive, subtype, 0 ) }
                </div>
                <div className={ S.nextIcon }>
                    { getType( isActive, subtype, 1 ) }
                </div>
                <div className={ S.text }>{ text }</div>
            </button>
        );
    }
}
