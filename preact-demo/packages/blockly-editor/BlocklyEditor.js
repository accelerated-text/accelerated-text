/// import Blockly          from '@code-dot-org/blockly';
import { h, Component } from 'preact';

import attribute        from '../blockly-blocks/attribute';
import Blockly          from '../blockly/Blockly';
import segment          from '../blockly-blocks/segment';

import S                from './BlocklyEditor.sass';


const toolbox = `
<xml style="display: none">
    <category name="Building blocks">
        <block type="segment" />
        <block type="attribute" />
    </category>
    <category name="Styling" />
</xml>
`;


export default class BlocklyEditor extends Component {

    onLoadBlockly = Blockly => {
        attribute( Blockly );
        segment( Blockly );
    }

    onMountBlockly = ( Blockly, { workspace }) => {

        console.log( Blockly );
        console.log( workspace );
    }

    render() {
        return (
            <div className={ S.className }>
                <Blockly
                    assetUrl="/blockly"
                    onLoad={ this.onLoadBlockly }
                    onMount={ this.onMountBlockly }
                    options={{ toolbox }}
                />
            </div>
        );
    }
}
