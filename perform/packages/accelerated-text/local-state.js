import gql                  from 'graphql-tag';


const __typename =          'AcceleratedTextView';
const id =                  'AcceleratedTextView';


export const acceleratedText = gql`
    query acceleratedText {
        acceleratedText @client {
            id
            openedDictionaryItem
            openedQuickSearch
        }
    }
`;

export const closeDictionaryItem = gql`
    mutation closeDictionaryItem {
      closeDictionaryItem @client
    }
`;

export const closeQuickSearch = gql`
    mutation closeQuickSearch {
        closeQuickSearch @client
    }
`;

export const openDictionaryItem = gql`
    mutation openDictionaryItem( $itemId: ID! ) {
        openDictionaryItem( itemId: $itemId ) @client
    }
`;

export const openQuickSearch = gql`
    mutation openQuickSearch {
        openQuickSearch @client
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
        closeQuickSearch:       ( _, __, { cache, getCacheKey }) => {
            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { openedQuickSearch: false },
            });
        },
        openDictionaryItem:     ( _, { itemId }, { cache, getCacheKey }) => {
            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { openedDictionaryItem: itemId },
            });
        },
        openQuickSearch:        ( _, __, { cache, getCacheKey }) => {
            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { openedQuickSearch: true },
            });
        },
    },
    Query: {
        acceleratedText: () => ({
            __typename,
            id,
            openedDictionaryItem:   null,
            openedQuickSearch:      false,
        }),
    },
};
