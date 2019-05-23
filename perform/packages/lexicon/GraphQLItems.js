import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages';
import { gql, GqlQuery }    from '../graphql/';


const lexiconSearchQuery = gql`
    query searchLexicon( $query: String ) {
        searchLexicon( query: $query ) {
            totalCount
            items { key synonyms }
        }
    }
`;

export default ({ query }) =>
    <GqlQuery query={ lexiconSearchQuery } variables={{ query }}>
        { ({ loading, error, data }) => (
            error
                ? <Error message={ error } />
            : loading
                ? <Loading message={ loading } />
            : ( data.searchLexicon && data.searchLexicon.items )
                ? <Info message="We have data!" />
                : <Info message="No results." />
        )}
    </GqlQuery>;
