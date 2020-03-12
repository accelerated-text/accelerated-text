import { blocks as B }      from '../nlg-blocks/';
import { createDictionaryItem } from '../graphql/mutations.graphql';
import { YES }              from '../usage/constants';

import getType              from './get-type';


const NEED_DICT_ITEM = [
    B.AMR,
    B.DictionaryItem,
    B.DictionaryItemModifier,
];


export default async ( result, graphqlClient ) => {

    const type =            getType( result, result.selectedType );

    const needsDictionaryItem = (
        NEED_DICT_ITEM.includes( type )
        && ! result.dictionaryItemId
    );

    if( needsDictionaryItem ) {
        const {
            data: {
                createDictionaryItem: item,
            },
        } = await graphqlClient.mutate({
            mutation:               createDictionaryItem,
            variables: {
                name:               result.text,
                partOfSpeech:       result.partOfSpeech || "N",
            },
        });
        return {
            ...result,
            dictionaryItemId:       item.id,
        };
    }

    return result;
};
