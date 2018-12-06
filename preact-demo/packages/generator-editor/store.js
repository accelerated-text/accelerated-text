import tokenizer            from '../tokenizer/tokenizer';

import tokensToBlockly      from './tokens-to-blockly';


const EXAMPLE_XML = '<xml xmlns="http://www.w3.org/1999/xhtml"><block type="segment" id="DK}9g^x|yLs%I]mh%jB{" x="30" y="39"><field name="GOAL">description</field><statement name="CHILDREN"><block type="unordered-list" id="t[Z%upfy@+5R(pr365xS"><mutation input_count="4"></mutation><value name="CHILD1"><block type="attribute" id="3Af_4b-FKj#Um%j@[eam"><field name="ATTRIBUTE">color</field></block></value><value name="CHILD2"><block type="attribute" id="pN2{}2ejgJ`5?of~%`X5"><field name="ATTRIBUTE">material</field></block></value><value name="CHILD3"><block type="attribute" id="wT1Z-fTn`I!v+^u`M.$]"><field name="ATTRIBUTE">make</field></block></value></block></statement></block></xml>';


export default {

    getInitialState: () => ({
        blocklyXml:         '',
        contextName:        null,
        dataSample:         null,
        generatorName:      'Example Generator',
        tokenizerError:     null,
        tokenizerLoading:   false,
    }),

    onChangeContext: ({ contextName }) => ({
        contextName,
    }),

    onChangeBlocklyWorkspace: blocklyXml => ({
        blocklyXml,
    }),

    onClickAddOnboardSegment: () => ({
        blocklyXml:         EXAMPLE_XML,
    }),

    onClickUpload: ({ dataSample }) => ({
        dataSample,
    }),

    onTokenizerRequest: () => ({
        tokenizerLoading:   true,
    }),

    onTokenizerResult: blocklyXml => ({
        blocklyXml,
        tokenizerError:     false,
        tokenizerLoading:   false,
    }),

    onTokenizerError: tokenizerError => ({
        tokenizerError,
        tokenizerLoading:   false,
    }),

    onSubmitTextExample: ({ text }, { events, state }) => {

        /// Prevent new requests while the previous one is not finished:
        if( state.tokenizerLoading ) {
            return;
        }

        events.onTokenizerRequest();

        tokenizer( text )
            .then( tokensToBlockly )
            .then( events.onTokenizerResult )
            .catch( err => {
                console.error( err );
                events.onTokenizerError( err );
            });
    },
};
