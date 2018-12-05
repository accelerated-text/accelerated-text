import { h, Component } from 'preact';

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

    Blockly =           null;
    workspace =         null;

    onLoadBlockly = Blockly => {

        this.Blockly =  Blockly;
        toolbox.registerBlocks( Blockly );
    };

    onMountWorkspace = workspace => {

        this.workspace =    window.workspace =  workspace;
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
