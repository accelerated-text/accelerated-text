import classnames           from 'classnames';
import { h, Component }     from 'preact';

import ValueIcon            from '../block-icons/Value';

import S                    from './Item.sass';


const COLORS = [
    S.dictionaryItemColor,
    S.modifierColor,
    S.amrColor,
];


const getColor = ( isActive, subtype, n ) =>
    COLORS[
        isActive
            ? (( subtype + n ) % COLORS.length )
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
                    <ValueIcon
                        color={ getColor( isActive, subtype, 0 ) }
                    />
                </div>
                <div className={ S.nextIcon }>
                    <ValueIcon
                        color={ getColor( isActive, subtype, 1 ) }
                    />
                </div>
                <div className={ S.text }>{ text }</div>
            </button>
        );
    }
}
