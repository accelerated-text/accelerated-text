import { QA }               from '../plan-editor/qa.constants';

import jsonToBlockly        from './json-to-blockly';


const EXAMPLE_XML = `
<xml xmlns="http://www.w3.org/1999/xhtml">
    <block type="segment" id="${ QA.EXAMPLE_XML }">
        <field name="text_type">description</field>
        <value name="items">
            <block type="sequence">
                <mutation value_count="3"></mutation>
                <value name="value_0"><block type="attribute"><field name="attribute_name">color</field></block></value>
                <value name="value_1"><block type="attribute"><field name="attribute_name">material</field></block></value>
                <value name="value_2"><block type="attribute"><field name="attribute_name">make</field></block></value>
            </block>
        </value>
    </block>
</xml>`;


export default {

    onboardCode: {

        onClickAddExample: ( _, { props }) => {

            props.onCreateXml.async( EXAMPLE_XML );
        },
    },

    tokenizer: {

        onCallResult: ( result, { props }) => {

            props.onCreateXml.async( jsonToBlockly( result ));
        },
    },
};
