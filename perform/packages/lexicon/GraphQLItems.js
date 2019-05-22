import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages';
import { gql, GqlQuery }    from '../graphql/';


const lexiconSearchQuery = gql`
    query lexiconSearchQuery( $query: String ) {
        lexicon( query: $query ) {
            totalCount
            items { key }
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
            : ( data.items && data.items.length )
                ? <Info message="We have data!" />
                : <Info message="No results." />
        )}
    </GqlQuery>;
