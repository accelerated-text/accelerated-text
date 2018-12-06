import { h, Component } from 'preact';
import PropTypes        from 'prop-types';

import attribute        from '../blocks/attribute';
import getToolbox       from '../blocks/get-toolbox';
import ResizableBlockly from '../preact-blockly/Resizable';
import segment          from '../blocks/segment';
import sentence         from '../blocks/sentence';
import token            from '../blocks/token';
import unorderedList    from '../blocks/unordered-list';

import S                from './BlocklyEditor.sass';


const toolbox = getToolbox([
    segment,
    unorderedList,
    attribute,
    sentence,
    token,
]);

const toolboxXml =      toolbox.toXmlString();


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
        toolbox.registerBlocks( Blockly );
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
