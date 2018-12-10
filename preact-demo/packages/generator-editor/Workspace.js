import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { provideBlocks }    from '../nlg-blocks/';
import ResizableBlockly     from '../preact-blockly/Resizable';

import S                    from './Workspace.sass';
import toolbox              from './toolbox.xml';


export default class GeneratorEditorWorkspace extends Component {

    static propTypes = {
        onChangeWorkspace:  PropTypes.func,
        workspaceXml:       PropTypes.object,
    };

    Blockly =               null;
    workspace =             null;

    onChangeWorkspace = () => {

        if( this.props.onChangeWorkspace ) {
            this.props.onChangeWorkspace(
                this.Blockly.Xml.domToText(
                    this.Blockly.Xml.workspaceToDom(
                        this.workspace
                    )
                )
            );
        }
    }

    onBlockly = Blockly => {

        this.Blockly =      Blockly;
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
            <div className={ S.className }>
                <ResizableBlockly
                    assetUrl="/blockly"
                    onBlockly={ this.onBlockly }
                    onWorkspace={ this.onWorkspace }
                    options={{ toolbox }}
                />
            </div>
        );
    }
}
