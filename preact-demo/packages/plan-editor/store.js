import domToGremlin         from '../blockly-gremlin/dom-to-gremlin';
import jsonToBlockly        from '../tokenizer/json-to-blockly';

import { QA }               from './qa.constants';


const EXAMPLE_XML = `
<xml xmlns="http://www.w3.org/1999/xhtml">
    <block type="segment" id="${ QA.EXAMPLE_XML }">
        <field name="goal">description</field>
        <statement name="first_statement">
            <block type="all-words">
                <mutation value_count="3"></mutation>
                <value name="value_0"><block type="attribute"><field name="attribute_name">color</field></block></value>
                <value name="value_1"><block type="attribute"><field name="attribute_name">material</field></block></value>
                <value name="value_2"><block type="attribute"><field name="attribute_name">make</field></block></value>
            </block>
        </statement>
    </block>
</xml>`;


export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        gremlinCode:        '',
        planName:           'Example Plan',
        workspaceDom:       null,
        workspaceXml:       '',
    }),

    planEditor: {
        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onChangeGremlinCode: gremlinCode => ({
            gremlinCode,
        }),

        onChangeWorkspace: ({ workspaceDom, workspaceXml }) => ({
            gremlinCode:    domToGremlin( workspaceDom ),
            workspaceDom,
            workspaceXml,
        }),

        onClickAddExample: () => ({
            workspaceXml:       EXAMPLE_XML,
        }),

        onClickUpload: ({ dataSample }) => ({
            dataSample,
        }),
    },

    tokenizer: {

        onCallResult: result => ({
            workspaceXml:   jsonToBlockly( result ),
        }),
    },
};
