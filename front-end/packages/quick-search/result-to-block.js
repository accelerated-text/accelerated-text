import { h }                from 'preact';

import { blocks as B }      from '../nlg-blocks/';
import Block                from '../block-component/BlockComponent';

import getType              from './get-type';


const getProps = {
    [B.AMR.type]: ({ concept, dictionaryItemId, text }) => ({
        mutation: {
            concept_id:     concept.id,
            concept_label:  concept.label,
            roles:          JSON.stringify( concept.roles ),
        },
        values: {
            dictionaryItem: <Block
                type={ B.DictionaryItem.type }
                mutation={{
                    id:     dictionaryItemId,
                    name:   text,
                }}
            />,
        },
    }),
    [B.AndOr.type]: () => ({
        fields: {
            operator:       'and',
        },
    }),
    [B.Cell.type]: ({ text }) => ({
        fields: {
            name:           text,
        },
    }),
    [B.CellModifier.type]: ({ text }) => ({
        fields: {
            name:           text,
        },
    }),
    [B.DefineVar.type]: ({ text }) => ({
        fields: {
            name:           text,
        },
    }),
    [B.DictionaryItem.type]: ({ dictionaryItemId, text }) => ({
        mutation: {
            id:             dictionaryItemId,
            name:           text,
        },
    }),
    [B.DictionaryItemModifier.type]: ({ dictionaryItemId, text }) => ({
        mutation: {
            id:             dictionaryItemId,
            name:           text,
        },
    }),
    [B.GetVar.type]: ({ text }) => ({
        fields: {
            name:           text,
        },
    }),
    [B.Quote.type]: ({ text }) => ({
        fields: {
            text,
        },
    }),
    [B.ValueComparison.type]: () => ({
        fields: {
            operator:       '=',
        },
    }),
};


export default result => {
    const type =            getType( result, result.selectedType ).type;
    const props = (
        getProps[type]
            ? getProps[type]( result )
            : null
    );
    return <Block type={ type } { ...props } />;
};
