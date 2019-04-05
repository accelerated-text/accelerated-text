import { h, Component }     from 'preact';

import { DT_TYPE }          from './constants';
import S                    from './DropTarget.sass';


export const isDragInType = evt =>
    evt.dataTransfer.getData( 'type' ) === DT_TYPE;


export default class DragInDropTarget extends Component {

    state = {
        isDraggedOver:      false,
    };

    onDragEnter = evt => {
        if( isDragInType( evt )) {
            evt.preventDefault();
            this.setState({ isDraggedOver: true });
        }
    }

    onDragLeave = evt => {
        if( isDragInType( evt )) {
            this.setState({ isDraggedOver: false });
        }
    }

    onDragOver = evt => {
        if( isDragInType( evt )) {
            evt.preventDefault();
        }
    }

    onDrop = evt => {

        if( !isDragInType( evt )) {
            return;
        }
        evt.preventDefault();

        this.setState({ isDraggedOver:  false });

        const {
            Blockly: { Xml },
            workspace,
        } = this.props;

        const dom =             Xml.textToDom( evt.dataTransfer.getData( 'xml' ));

        /// Set position:
        const svgOffset =       workspace.getParentSvg().getBoundingClientRect();
        const originOffset =    workspace.getOriginOffsetInPixels();

        dom.firstChild.setAttribute( 'x', evt.x - svgOffset.x - originOffset.x - 20 );
        dom.firstChild.setAttribute( 'y', evt.y - svgOffset.y - originOffset.y - 20 );

        /// Insert into the workspace:
        Xml.domToWorkspace( dom, workspace );
    }

    render({ children }, { isDraggedOver }) {
        return (
            <div
                className={ S.className }
                onDragEnter={ this.onDragEnter }
            >
                { children }
                { isDraggedOver &&
                    <div
                        className={ S.dragOverlay }
                        onDragEnter={ this.onDragEnter }
                        onDragOver={ this.onDragOver }
                        onDragLeave={ this.onDragLeave }
                        onDrop={ this.onDrop }
                    />
                }
            </div>
        );
    }
}
