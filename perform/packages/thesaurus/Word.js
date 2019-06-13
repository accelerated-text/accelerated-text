import classnames               from 'classnames';
import { h, Component }         from 'preact';
import PropTypes                from 'prop-types';

import S                        from './Word.sass';


export default class ThesaurusWord extends Component {

    static propTypes = {
        className:              PropTypes.string,
        onClick:                PropTypes.func.isRequired,
        word:                   PropTypes.object.isRequired,
    };
    
    state = {
        isDragged:              false,
    };

    onClick = () =>
        this.props.onClick( this.props.word );

    onDragStart = evt => {

        const dt =          evt.dataTransfer;
        dt.setData( 'type', 'text/plain' );
        dt.setData( 'text',  this.props.word.text );

        this.setState({ isDragged: true });
    }

    onDragEnd = evt =>
        this.setState({ isDragged: false });

    render({ className, word }, { isDragged }) {
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
                <div onClick={ this.onClick }>{ word.text }</div>
            </li>
        );
    }
}
