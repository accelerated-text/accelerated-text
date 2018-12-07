import { h, Component } from 'preact';
import PropTypes        from 'prop-types';

import { provide }      from '../nlg-blocks/';
import ResizableBlockly from '../preact-blockly/Resizable';
import toolboxXml       from '../generator-editor/toolbox.xml';

import S                from './BlocklyEditor.sass';


export default class BlocklyEditor extends Component {

    static propTypes = {
        onChangeWorkspace:  PropTypes.func,
        workspaceXml:       PropTypes.object,
    };

    Blockly =           null;
    workspace =         null;

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

        this.Blockly =  Blockly;
        provide( Blockly );
    };

    onWorkspace = workspace => {

        const { workspaceXml } =    this.props;

        this.workspace =    window.workspace =  workspace;

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
                    options={{ toolbox: toolboxXml }}
                />
            </div>
        );
    }
}
