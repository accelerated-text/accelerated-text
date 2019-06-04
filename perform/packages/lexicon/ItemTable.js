import classnames           from 'classnames';
import gql                  from 'graphql-tag';
import { h }                from 'preact';

import DebouncedSearchQuery from '../graphql/DebouncedSearchQuery';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { QA }               from '../tests/constants';

import S                    from './ItemTable.sass';
import ItemRow              from './ItemRow';


const searchLexicon = gql`
    query searchLexicon( $searchQuery: String $offset: Int! ) {
        results: searchLexicon( query: $searchQuery offset: $offset ) {
            items { key synonyms }
            offset
            totalCount
        }
    }
`;

const wildcardQuery =       query => `${ query || '' }*`;


export default ({
    newItem,
    newItemSaved,
    onCancelNew,
    onSaveNew,
    query,
}) => (
    <DebouncedSearchQuery
        gqlQuery={ searchLexicon }
        searchQuery={ wildcardQuery( query ) }
    >
        { ({ error, items, loading, offset, onFetchMore, totalCount }) => {

            const areItemsEditable =    !! ( ! loading || offset );
            const emptyResult =         items && ! items.length;
            const hasItems =            !! ( items && items.length );
            const hasMore =             hasItems && totalCount && items.length < totalCount;
            const showBottom =          emptyResult || error || hasMore || loading;

            return (
                <table className={ classnames( S.className, QA.LEXICON_LIST ) }>
                    <thead>
                        <tr>
                            <th />
                            <th>ID</th>
                            <th className={ S.phrases }>
                                <div>
                                    <span className={ S.label }>Phrases</span>
                                    <span className={ S.status }>
                                        { error && <Error justIcon message={ error } /> }
                                        { loading && <Loading /> }
                                    </span>
                                </div>
                            </th>
                        </tr>
                    </thead>
                    { newItem &&
                        <tbody key="newItem" className={ S.newItem }>
                            <ItemRow
                                className={ classnames( S.newItemRow, QA.LEXICON_NEW_ITEM ) }
                                editing={ !newItemSaved }
                                isEditable={ areItemsEditable }
                                item={ newItem }
                                key={ newItem.key || 'NEW ITEM' }
                                onCancel={ onCancelNew }
                                onSave={ onSaveNew }
                            />
                        </tbody>
                    }
                    { hasItems &&
                        <tbody
                            key="items"
                            className={ classnames( S.items, areItemsEditable && S.areItemsEditable ) }
                        >
                            { items.map( item =>
                                <ItemRow
                                    className={ QA.LEXICON_ITEM }
                                    isEditable={ areItemsEditable }
                                    item={ item }
                                    key={ item.key }
                                />
                            )}
                        </tbody>
                    }
                    { showBottom &&
                        <tbody key="bottom" className={ S.bottom }>
                            <tr>
                                <td colspan="3">
                                    { error && <Error message={ error } /> }
                                    { loading
                                        ? <Loading />
                                    : hasMore
                                        ? <a
                                            children="🔽 Show more results"
                                            className={ classnames( S.more, QA.LEXICON_MORE ) }
                                            onClick={ onFetchMore }
                                        />
                                    : emptyResult
                                        ? <Info
                                            className={ QA.LEXICON_NO_ITEMS }
                                            message="No results found"
                                        />
                                        : null
                                    }
                                </td>
                            </tr>
                        </tbody>
                    }
                </table>
            );
        }}
    </DebouncedSearchQuery>
);
