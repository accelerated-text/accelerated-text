import classnames       from 'classnames';
import { h, Component } from 'preact';
import PropTypes        from 'prop-types';

import injectBlockly    from '../inject-blockly/inject-blockly';

import S                from './Resizable.sass';


export default class ResizableBlockly extends Component {

    static defaultProps = {
        language:       'en',
    };

    static propTypes = {
        assetUrl:       PropTypes.string.required,
        className:      PropTypes.string,
        language:       PropTypes.string,
        onBlockly:      PropTypes.func,
        onWorkspace:    PropTypes.func,
        options:        PropTypes.object,
    };

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
            this.Blockly.svgResize( this.workspace );
        }
    }

    async componentDidMount() {

        const {
            assetUrl,
            language,
            onBlockly,
            onWorkspace,
            options,
        } = this.props;

        this.Blockly = await injectBlockly({
            language,
            prefix:         assetUrl,
        });

        onBlockly && onBlockly( this.Blockly );

        this.workspace = this.Blockly.inject(
            this.workspaceElement,
            {
                media:      `${ assetUrl }/media/`,
                ...options,
            },
        );

        onWorkspace && onWorkspace( this.workspace );

        window.addEventListener( 'resize', this.onResize, false );
        this.onResize();
    }

    shouldComponentUpdate() {

        /// Once rendered, the DOM elements should NOT be modified by Preact:
        return false;
    }

    componentWillUnmount() {

        window.removeEventListener( 'resize', this.onResize, false );
    }

    render() {
        return (
            <div
                className={ classnames( S.className, this.props.className ) }
                ref={ el => this.rootElement = el }
            >
                <div
                    className={ S.workspace }
                    ref={ el => this.workspaceElement = el }
                />
            </div>
        );
    }
}
