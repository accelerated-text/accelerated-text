import pTap                 from 'p-tap';

import tokenizer            from '../tokenizer/tokenizer';
import tokensToBlockly      from '../tokenizer/json-to-blockly';
import variantsApi          from '../variants-api/';

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
        tokenizerError:     null,
        tokenizerLoading:   false,
        variants:           null,
        variantsError:      false,
        variantsLoading:    false,
        workspaceXml:       '',
    }),

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

    onGetVariants: () => ({
        variantsLoading:    true,
    }),

    onGetVariantsError: variantsError => ({
        variantsError,
        variantsLoading:    false,
    }),

    onGetVariantsSuccess: variants => ({
        variants,
        variantsError:      false,
        variantsLoading:    false,
    }),

    onTokenizerCall: () => ({
        tokenizerLoading:   true,
    }),

    onTokenizerError: tokenizerError => ({
        tokenizerError,
        tokenizerLoading:   false,
    }),

    onTokenizerResult: workspaceXml => ({
        tokenizerError:     false,
        tokenizerLoading:   false,
        workspaceXml,
    }),

    /// Adapter functions:

    getVariants: ( workspaceXml, { events, state }) => {

        if( state.variantsLoading ) {
            return;
        }

        events.onChangeWorkspace( workspaceXml );
        events.onGetVariants();

        variantsApi.getForDataSample({
            dataSampleId:   'TODO_REPLACE',
            documentPlanId: 'TODO_REPLACE',
        })
            .then( events.onGetVariantsSuccess )
            .catch( pTap( console.error ))
            .catch( events.onGetVariantsError );
    },

    onSubmitTextExample: ({ text }, { events, state }) => {

        /// Prevent new requests while the previous one is not finished:
        if( state.tokenizerLoading ) {
            return;
        }

        events.onTokenizerCall();

        tokenizer( text )
            .then( tokensToBlockly )
            .then( events.onTokenizerResult )
            .catch( pTap( console.error ))
            .catch( events.onTokenizerError );
    },
};
