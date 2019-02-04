import { QA }               from '../plan-editor/qa.constants';

import jsonToBlockly        from './json-to-blockly';


const EXAMPLE_XML = `<xml xmlns="http://www.w3.org/1999/xhtml"><block type="Document-plan" id="${ QA.EXAMPLE_XML }" deletable="false" x="18" y="180"><statement name="segments"><block type="Segment"><mutation value_count="3" value_sequence="value_"></mutation><field name="text_type">description</field><value name="value_0"><block type="Product"><mutation value_count="3" value_sequence="value_"></mutation><value name="name"><block type="Cell"><field name="name">Name</field></block></value><value name="value_0"><block type="Relationship"><mutation value_count="4" value_sequence="value_"></mutation><field name="relationshipType">provides</field><value name="value_0"><block type="Cell"><field name="name">Main Feature</field></block></value><value name="value_1"><block type="Cell"><field name="name">Secondary feature</field></block></value><value name="value_2"><block type="Cell"><field name="name">Style</field></block></value></block></value></block></value><value name="value_1"><block type="Product-component"><mutation value_count="2" value_sequence="value_"></mutation><value name="name"><block type="Cell"><field name="name">Lacing</field></block></value><value name="value_0"><block type="Relationship"><mutation value_count="2" value_sequence="value_"></mutation><field name="relationshipType">consequence</field><value name="value_0"><block type="If-then-else"><mutation else_if_count="0"></mutation><value name="if"><block type="Value-comparison"><field name="operator">=</field><value name="value1"><block type="Cell"><field name="name">Lacing</field></block></value><value name="value2"><block type="Quote"><field name="text">premium</field></block></value></block></value><value name="then"><block type="One-of-synonyms"><mutation value_count="3"></mutation><value name="value_0"><block type="Quote"><field name="text">snug fit for everyday wear</field></block></value><value name="value_1"><block type="Quote"><field name="text">never gets into a knot</field></block></value></block></value></block></value></block></value></block></value></block></statement></block></xml>`;


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
