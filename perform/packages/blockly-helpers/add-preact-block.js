import { h }                from 'preact';
import renderToString       from 'preact-render-to-string';


export default ( workspace, element, { Xml } = window.Blockly ) =>
    workspace.getBlockById(
        Xml.domToWorkspace(
            Xml.textToDom(
                renderToString( <xml children={ element } /> )
            ),
            workspace,
        )[ 0 ]
    );
