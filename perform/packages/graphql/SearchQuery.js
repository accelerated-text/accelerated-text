import { h }                from 'preact';
import { path }             from 'ramda';

import { GqlQuery }         from './index';
import concatResultsInPath  from './concat-results-in-path';


/*  Example gqlQuery:

    query exampleQuery( $searchQuery: String $offset: String! ) {
        results: yourQuery( searchQuery: $searchQuery offset: $offset ) {
            items { id value }
            offset
            totalCount
        }
    }

    Example usage:

    <SearchQuery
        ResultsComponent={ Results }
        gqlQuery={ exampleQuery }
        searchQuery={ userInputString }
    />

    Results example:

    ({ searchError, searchItems, searchLoadint, onFetchMore }) =>
        <div>
            { searchItems && searchItems.map( item => <div>{ item.value }</div> ) }
            <button onClick={ onFetchMore }>Load more</button>
        </div>;

*/

const PATHS = {
    items:                  [ 'results', 'items' ],
    offset:                 [ 'results', 'offset' ],
    totalCount:             [ 'results', 'totalCount' ],
};


export default ({ gqlQuery, searchQuery, ResultsComponent, ...props }) =>
    <GqlQuery
        fetchPolicy="cache-and-network"
        notifyOnNetworkStatusChange
        query={ gqlQuery }
        variables={{
            offset:         0,
            searchQuery,
        }}
    >
        { ({ error, data, loading, fetchMore }) =>
            <ResultsComponent
                onFetchMore={ () => fetchMore({
                    variables: {
                        offset:     data.results.items.length,
                    },
                    updateQuery:    concatResultsInPath( PATHS.items ),
                }) }
                searchError={ error }
                searchItems={ path( PATHS.items, data ) }
                searchLoading={ loading }
                searchOffset={ path( PATHS.offset, data ) }
                searchTotalCount={ path( PATHS.totalCount, data ) }
                { ...props }
            />
        }
    </GqlQuery>;
