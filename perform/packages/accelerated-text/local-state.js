import gql                  from 'graphql-tag';


const __typename =          'AcceleratedTextView';
const id =                  'AcceleratedTextView';


export const acceleratedText = gql`
    query acceleratedText {
        acceleratedText @client {
            id
            openedDictionaryItem
        }
    }
`;

export const closeDictionaryItem = gql`
    mutation closeDictionaryItem {
      closeDictionaryItem @client
    }
`;

export const openDictionaryItem = gql`
    mutation openDictionaryItem( $itemId: ID! ) {
        openDictionaryItem( itemId: $itemId ) @client
    }
`;


export default {
    Mutation: {
        closeDictionaryItem:    ( _, __, { cache, getCacheKey }) => {
            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { openedDictionaryItem: null },
            });
        },
        openDictionaryItem:     ( _, { itemId }, { cache, getCacheKey }) => {
            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { openedDictionaryItem: itemId },
            });
        },
    },
    Query: {
        acceleratedText: () => ({
            __typename,
            id,
            openedDictionaryItem:   null,
        }),
    },
};
