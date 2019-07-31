import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';
import renderToString       from 'preact-render-to-string';

import Block                from '../block-component/BlockComponent';
import ValueIcon            from '../block-icons/Value';

import { DT_TYPE }          from './constants';
import S                    from './DragInBlock.sass';


export default class DragInBlock extends Component {

    static propTypes = {
        className:          PropTypes.string,
        comment:            PropTypes.string,
        fields:             PropTypes.object,
        type:               PropTypes.string.isRequired,
    };

    state = {
        isDragged:          false,
    };

    onDragStart = evt => {

        const dt =          evt.dataTransfer;
        dt.setData( 'type', DT_TYPE );
        dt.setData(
            'xml',
            renderToString( <xml><Block { ...this.props } /></xml> ),
        );

        this.setState({ isDragged: true });
    }

    onDragEnd = evt =>
        this.setState({ isDragged: false });

    render(
        { className, color = S.fillColor },
        { isDragged, isOverDrop },
    ) {
        return (
            <div
                className={ classnames(
                    S.className,
                    className,
                    isDragged && S.isDragged,
                ) }
                draggable="true"
                onDragStart={ this.onDragStart }
                onDragEnd={ this.onDragEnd }
            >
                <ValueIcon color={ color } />
            </div>
        );
    }
}
