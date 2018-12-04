import { h, Component } from 'preact';

import loadBlockly      from './load';
import S                from './Blockly.sass';


export default class Blockly extends Component {

    Blockly =           null;
    lastOffsetHeight =  0;
    lastOffsetWidth =   0;
    rootElement =       null;
    workspace =         null;
    workspaceElement =  null;

    onResize = () => {

        const {
            offsetHeight,
            offsetWidth,
        } = this.rootElement;

        const resized = (
            offsetHeight !== this.lastOffsetHeight
            || offsetWidth !== this.lastOffsetWidth
        );

        if( resized ) {
            this.lastOffsetHeight =                 offsetHeight;
            this.lastOffsetWidth =                  offsetWidth;
            this.workspaceElement.style.height =    `${ offsetHeight }px`;
            this.workspaceElement.style.width =     `${ offsetWidth }px`;
        }

        this.Blockly.svgResize( this.workspace );
    }

    async componentDidMount() {

        const {
            assetUrl,
            onLoad,
            onMount,
            options,
        } = this.props;

        this.Blockly =  await loadBlockly({ prefix: assetUrl });

        onLoad && onLoad( this.Blockly );

        this.workspace = this.Blockly.inject( this.workspaceElement, {
            media:      `${ assetUrl }/media/`,
            ...options,
        });

        onMount && onMount( this.Blockly, {
            workspace:  this.workspace,
        });

        window.addEventListener( 'resize', this.onResize, false );
        this.onResize();
    }

    shouldComponentUpdate() {

        return false;
    }

    render() {
        return (
            <div className={ S.className } ref={ el => this.rootElement = el }>
                <div className={ S.workspace } ref={ el => this.workspaceElement = el } />
            </div>
        );
    }
}
