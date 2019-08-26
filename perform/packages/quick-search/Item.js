import classnames           from 'classnames';
import { h, Component }     from 'preact';

import AMR                  from '../nlg-blocks/AMR';
import DictionaryItem       from '../nlg-blocks/Dictionary-item';
import Modifier             from '../nlg-blocks/Modifier';
import Quote                from '../nlg-blocks/Quote';

import S                    from './Item.sass';


const TYPES = [
    DictionaryItem,
    Modifier,
    AMR,
    Quote,
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
            <div
                className={ classnames(
                    S.className,
                    isActive && S.isActive,
                    className,
                ) }
                onClick={ this.onClick }
            >
                <div className={ S.icon }>
                    { getType( isActive, subtype, 0 ).icon }
                </div>
                <div className={ S.nextIcon }>
                    { getType( isActive, subtype, 1 ).icon }
                </div>
                <div className={ S.text }>{ text }</div>
            </div>
        );
    }
}
