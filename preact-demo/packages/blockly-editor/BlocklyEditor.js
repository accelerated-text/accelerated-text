import { h, Component } from 'preact';
import PropTypes        from 'prop-types';

import attribute        from '../blockly-blocks/attribute';
import getToolbox       from '../blockly-blocks/get-toolbox';
import Blockly          from '../blockly/Blockly';
import segment          from '../blockly-blocks/segment';
import unorderedList    from '../blockly-blocks/unordered-list';

import S                from './BlocklyEditor.sass';


const toolbox = getToolbox([
    segment,
    unorderedList,
    attribute,
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

    onLoadBlockly = Blockly => {

        this.Blockly =  Blockly;
        toolbox.registerBlocks( Blockly );
    };

    onMountWorkspace = workspace => {

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
                <Blockly
                    assetUrl="/blockly"
                    onLoad={ this.onLoadBlockly }
                    onMount={ this.onMountWorkspace }
                    options={{ toolbox: toolboxXml }}
                />
            </div>
        );
    }
}
