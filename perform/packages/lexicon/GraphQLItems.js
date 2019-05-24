import { h }                from 'preact';

import { gql }              from '../graphql/';
import SearchQuery          from '../graphql/SearchQuery';

import ItemTable            from './ItemTable';


const searchLexicon = gql`
    query searchLexicon( $searchQuery: String $offset: String! ) {
        results: searchLexicon( query: $searchQuery offset: $offset ) {
            items { key synonyms }
            offset
            totalCount
        }
    }
`;

export default ({ E, lexicon, query }) =>
    <SearchQuery
        E={ E }
        ResultsComponent={ ItemTable }
        gqlQuery={ searchLexicon }
        lexicon={ lexicon }
        searchQuery={ `${ query || '' }*` }
    />;
