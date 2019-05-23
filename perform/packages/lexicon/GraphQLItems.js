import { h, Component }     from 'preact';
import {
    assocPath,
    pathOr,
}   from 'ramda';

import { gql, GqlQuery }    from '../graphql/';

import ItemTable            from './ItemTable';


const searchLexicon = gql`
    query searchLexicon( $query: String $offset: String! ) {
        results: searchLexicon( query: $query offset: $offset ) {
            items { key synonyms }
            offset
            totalCount
        }
    }
`;

const searchLexiconUpdateQuery = ( prevResult, { fetchMoreResult }) => (
    fetchMoreResult
        ? assocPath(
            [ 'results', 'items' ],
            [
                ...prevResult.results.items,
                ...fetchMoreResult.results.items,
            ],
            prevResult
        )
        : prevResult
);


class ItemTableWrapper extends Component {

    onClickMore = () => {
        this.props.fetchMore({
            variables: {
                offset:     this.props.data.results.items.length,
            },
            updateQuery:    searchLexiconUpdateQuery,
        });
    };

    render({ E, lexicon, error, data, loading }) {
        return (
            <ItemTable
                E={ E }
                lexicon={ lexicon }
                items={ pathOr([], [ 'results', 'items' ], data ) }
                onClickMore={ this.onClickMore }
                requestOffset={ pathOr( 0, [ 'results', 'offset' ], data ) }
                resultsError={ error }
                resultsLoading={ loading }
                totalCount={ pathOr( 0, [ 'results', 'totalCount' ], data ) }
            />
        );
    }
}

export default ({ E, lexicon }) =>
    <GqlQuery
        fetchPolicy="cache-and-network"
        notifyOnNetworkStatusChange
        query={ searchLexicon }
        variables={{
            offset:         0,
            query:          `${ lexicon.query || '' }*`,
        }}
    >
        { ({ error, data, fetchMore, loading }) =>
            <ItemTableWrapper
                E={ E }
                lexicon={ lexicon }
                error={ error }
                data={ data }
                fetchMore={ fetchMore }
                loading={ loading }
            />
        }
    </GqlQuery>;
