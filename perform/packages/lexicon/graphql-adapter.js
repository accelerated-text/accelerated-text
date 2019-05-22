import { gql, gqlClient }   from '../graphql/';


const lexiconSearchQuery = gql`
    query lexiconSearchQuery( $query: String ) {
        lexicon( query: $query ) {
            totalCount
            items { key }
        }
    }
`;


export default {

    lexicon: {
        onGet: ( _, { getStoreState }) => {
            const {
                query,
            } = getStoreState( 'lexicon' );

            gqlClient.query({
                query:      lexiconSearchQuery,
                variables:  { query },
            })
                .then( result => console.log( 'query result', result, JSON.stringify( result.data )))
                .catch( error => console.error( 'query error', error ));
        },
    },
};
