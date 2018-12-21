import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { provideBlocks }    from '../nlg-blocks/';
import ResizableBlockly     from '../preact-blockly/Resizable';

import toolbox              from './toolbox.xml';


export default class PlanEditorWorkspace extends Component {

    static propTypes = {
        onChangeWorkspace:  PropTypes.func,
        workspaceXml:       PropTypes.object,
    };

    Blockly =               null;
    workspace =             null;

    onChangeWorkspace = () => {

        if( this.props.onChangeWorkspace ) {

            const { Xml } =         this.Blockly;
            const workspaceDom =    Xml.workspaceToDom( this.workspace );
            const workspaceXml =    Xml.domToText( workspaceDom );

            this.props.onChangeWorkspace({
                workspaceDom,
                workspaceXml,
            });
        }
    }

    onBlockly = Blockly => {

        this.Blockly =      Blockly;

        /// Set Style for the workspace
        Blockly.HSV_SATURATION =    0.55;
        Blockly.HSV_VALUE =         0.6;

        provideBlocks( Blockly );
    };

    onWorkspace = workspace => {

        const { workspaceXml } =    this.props;

        this.workspace =            workspace;

        if( workspaceXml ) {
            this.Blockly.Xml.domToWorkspace(
                this.Blockly.Xml.textToDom( workspaceXml ),
                workspace,
            );
        }

        this.workspace.addChangeListener( this.onChangeWorkspace );
    }

    render() {
        return (
            <ResizableBlockly
                assetUrl="/blockly"
                onBlockly={ this.onBlockly }
                onWorkspace={ this.onWorkspace }
                options={{
                    horizontalLayout:   true,
                    toolbox,
                    trashcan:           false,
                }}
            />
        );
    }
}
