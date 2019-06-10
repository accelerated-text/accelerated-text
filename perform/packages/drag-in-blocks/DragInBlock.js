import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { DT_TYPE }          from './constants';
import getBlockXml          from './get-block-xml';
import S                    from './DragInBlock.sass';


export default class DragInBlock extends Component {

    static propTypes = {
        className:          PropTypes.string,
        comment:            PropTypes.string,
        fields:             PropTypes.object,
        text:               PropTypes.string,
        type:               PropTypes.string.isRequired,
    };

    state = {
        isDragged:          false,
    };

    onDragStart = evt => {

        const dt =          evt.dataTransfer;
        dt.setData( 'type', DT_TYPE );
        dt.setData( 'xml',  getBlockXml( this.props )); /// uses { comment, fields, type }

        this.setState({ isDragged: true });
    }

    onDragEnd = evt =>
        this.setState({ isDragged: false });

    render(
        { className, color = S.fillColor, text, width = 180 },
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
                <svg height="28" width={ width }>
                    <g transform="translate( 8, 0 )">
                        <path
                            d={ `m 0,0 H ${ width } v 31 H 0 V 20 c 0,-10 -8,8 -8,-7.5 s 8,2.5 8,-7.5 z` }
                            fill={ color }
                        />
                        { text &&
                            <text children={ text } transform="translate( 8, 18 )" />
                        }
                    </g>
                </svg>
            </div>
        );
    }
}
