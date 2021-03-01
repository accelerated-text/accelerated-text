import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';
import renderToString       from 'preact-render-to-string';

import Block                from '../block-component/BlockComponent';

import { DT_TYPE }          from './constants';
import S                    from './DragInBlock.sass';


const BlockType = ( props, propName, componentName ) =>
    PropTypes.checkPropTypes({
        block:              PropTypes.func.isRequired,
        icon:               PropTypes.element.isRequired,
        type:               PropTypes.string.isRequired,
    }, {
        ...props[propName],
        block:              props[propName],
    },
    propName,
    componentName,
    );


export default class DragInBlock extends Component {

    static propTypes = {
        block:              BlockType,
        className:          PropTypes.string,
        comment:            PropTypes.string,
        fields:             PropTypes.object,
        mutation:           PropTypes.object,
        values:             PropTypes.object,
    };

    state = {
        isDragged:          false,
    };

    onDragStart = evt => {
        const dt =          evt.dataTransfer;
        dt.setData( 'type', DT_TYPE );
        dt.setData(
            'xml',
            renderToString(
                <xml>
                    <Block
                        type={ this.props.block.type }
                        { ...this.props }
                    />
                </xml>,
            ),
        );

        this.setState({ isDragged: true });
    }

    onDragEnd = evt =>
        this.setState({ isDragged: false });

    render(
        { block: { icon }, className },
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
                { icon }
            </div>
        );
    }
}
