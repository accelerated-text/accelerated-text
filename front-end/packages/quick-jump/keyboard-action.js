import { focusWorkspace }   from '../blockly-helpers/';

import QuickJump            from './QuickJump';


export default ({
    Blockly,
    bottomBarContext: {
        closeBar,
        onCloseBar,
        openComponentBar,
    },
    workspace,
}) => {
    openComponentBar( QuickJump, {
        Blockly,
        onDone:         closeBar,
        selected:       Blockly.selected,
        workspace,
    });
    onCloseBar(() =>
        focusWorkspace( workspace )
    );
};
