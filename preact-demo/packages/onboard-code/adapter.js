import { QA }               from '../plan-editor/qa.constants';

import jsonToBlockly        from './json-to-blockly';

const EXAMPLE_XML = `<xml xmlns="http://www.w3.org/1999/xhtml"><block type="document-plan"  id="${ QA.EXAMPLE_XML }" x="12" y="254"><statement name="segments"><block type="segment"><mutation value_count="3"></mutation><field name="text_type">description</field><value name="value_0"><block type="product"><mutation value_count="3"></mutation><value name="name"><block type="attribute"><field name="attribute_name">Name</field></block></value><value name="value_0"><block type="relationship"><mutation value_count="3"></mutation><field name="type">provides</field><value name="value_0"><block type="attribute"><field name="attribute_name">Main Feature</field></block></value><value name="value_1"><block type="attribute"><field name="attribute_name">Secondary feature</field></block></value></block></value><value name="value_1"><block type="rhetorical"><field name="type">elaboration</field><value name="value_0"><block type="attribute"><field name="attribute_name">Style</field></block></value></block></value></block></value><value name="value_1"><block type="product-component"><mutation value_count="2"></mutation><value name="name"><block type="attribute"><field name="attribute_name">Lacing</field></block></value><value name="value_0"><block type="relationship"><mutation value_count="2"></mutation><field name="type">consequence</field><value name="value_0"><block type="if-then-else"><mutation else_if_count="0"></mutation><value name="if"><block type="number-comparison"><field name="operator">=</field><value name="value1"><block type="attribute"><field name="attribute_name">Lacing</field></block></value><value name="value2"><block type="quote"><field name="text">premium</field></block></value></block></value><value name="then"><block type="any-count-from"><mutation value_count="4"></mutation><field name="count">1</field><value name="value_0"><block type="quote"><field name="text">snug fit for everyday wear</field></block></value><value name="value_1"><block type="quote"><field name="text">never gets into a knot</field></block></value><value name="value_2"><block type="quote"><field name="text">remains firmly tied</field></block></value></block></value></block></value></block></value></block></value></block></statement></block></xml>`;

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
