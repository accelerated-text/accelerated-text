const EXAMPLE_XML =     '<xml xmlns="http://www.w3.org/1999/xhtml"><block type="segment" id="DK}9g^x|yLs%I]mh%jB{" x="42" y="101"><field name="GOAL">pitch</field><statement name="CHILDREN"><block type="unordered-list" id="t[Z%upfy@+5R(pr365xS"><mutation input_count="2"></mutation><value name="CHILD1"><block type="attribute" id="3Af_4b-FKj#Um%j@[eam"><field name="ATTRIBUTE">material</field></block></value></block></statement></block></xml>';


export default {

    getInitialState:    () => ({
        xml:            EXAMPLE_XML,
    }),

    onChangeXml:        xml => ({ xml }),
};
