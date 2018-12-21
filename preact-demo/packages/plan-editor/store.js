import { QA }               from './qa.constants';


const EXAMPLE_XML = `
<xml xmlns="http://www.w3.org/1999/xhtml">
    <block type="segment" id="${ QA.EXAMPLE_XML }">
        <field name="goal">description</field>
        <statement name="first_child">
            <block type="all-words">
                <mutation value_count="3"></mutation>
                <value name="value_0"><block type="attribute"><field name="name">color</field></block></value>
                <value name="value_1"><block type="attribute"><field name="name">material</field></block></value>
                <value name="value_2"><block type="attribute"><field name="name">make</field></block></value>
            </block>
        </statement>
    </block>
</xml>`;


export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        planName:           'Example Plan',
        workspaceXml:       '',
    }),

    planEditor: {

        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onChangeWorkspace: workspaceXml => ({
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

        onCallResult: workspaceXml => ({
            workspaceXml,
        }),
    },
};
