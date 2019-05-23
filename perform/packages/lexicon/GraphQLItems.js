import { h }                from 'preact';
import { pathOr }           from 'ramda';

import { gql, GqlQuery }    from '../graphql/';

import ItemTable            from './ItemTable';


const searchLexicon = gql`
    query searchLexicon( $query: String ) {
        searchLexicon( query: $query ) {
            items { key synonyms }
            offset
            totalCount
        }
    }
`;

export default ({ E, lexicon }) =>
    <GqlQuery query={ searchLexicon } variables={{ query: `${ lexicon.query || '' }*` }}>
        { ({ loading, error, data }) =>
            <ItemTable
                E={ E }
                lexicon={ lexicon }
                items={ pathOr([], [ 'searchLexicon', 'items' ], data ) }
                requestOffset={ pathOr( 0, [ 'searchLexicon', 'offset' ], data ) }
                resultsError={ error }
                resultsLoading={ loading }
                totalCount={ pathOr( 0, [ 'searchLexicon', 'totalCount' ], data ) }
            />
        }
    </GqlQuery>;
