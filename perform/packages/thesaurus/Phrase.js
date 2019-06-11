import classnames               from 'classnames';
import { h, Component }         from 'preact';

import S                        from './Phrase.sass';


export default class ThesaurusPhrase extends Component {
    
    state = {
        isDragged:              false,
    };

    onClick = () =>
        this.props.onClick( this.props.phrase );

    onDragStart = evt => {

        const dt =          evt.dataTransfer;
        dt.setData( 'type', 'text/plain' );
        dt.setData( 'text',  this.props.phrase.text );

        this.setState({ isDragged: true });
    }

    onDragEnd = evt =>
        this.setState({ isDragged: false });

    render({ className, phrase }, { isDragged }) {
        return (
            <li
                className={ classnames(
                    S.className,
                    className,
                    isDragged && S.isDragged,
                ) }
                draggable="true"
                onDragStart={ this.onDragStart }
                onDragEnd={ this.onDragEnd }
            >
                <div onClick={ this.onClick }>{ phrase.text }</div>
            </li>
        );
    }
}
