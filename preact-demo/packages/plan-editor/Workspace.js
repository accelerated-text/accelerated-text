import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import DocumentPlan         from '../nlg-blocks/Document-plan';
import { provideBlocks }    from '../nlg-blocks/';
import ResizableBlockly     from '../preact-blockly/Resizable';

import S                    from './Workspace.sass';
import toolbox              from './toolbox.xml';


export default class PlanEditorWorkspace extends Component {

    static propTypes = {
        onChangeWorkspace:  PropTypes.func,
        workspaceXml:       PropTypes.object,
    };

    Blockly =               null;
    workspace =             null;

    onChangeWorkspace = evt => {
        console.log( 'onChangeWorkspace', evt.type, evt );

        if( this.props.onChangeWorkspace ) {

            const {
                Blockly: { Events, Xml },
                workspace,
            } = this;

            const skipTypes = [
                Events.BLOCK_MOVE,
                Events.MOVE,
                Events.UI,
            ];

            if( !skipTypes.includes( evt.type )) {
                this.props.onChangeWorkspace({
                    documentPlan:
                        workspace
                            .getTopBlocks()
                            .find( block => block.type === DocumentPlan.type )
                            .toNlgJson(),
                    workspaceXml:
                        Xml.domToText(
                            Xml.workspaceToDom( workspace )
                        ),
                });
            }
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
                className={ S.className }
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
