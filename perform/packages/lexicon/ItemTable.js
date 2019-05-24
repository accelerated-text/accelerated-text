import classnames           from 'classnames';
import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { QA }               from '../tests/constants';

import S                    from './ItemTable.sass';
import ItemRow              from './ItemRow';


export default ({
    E,
    lexicon: {
        newItem,
        newItemSaved,
    },
    searchItems = [],
    onFetchMore,
    searchOffset = 0,
    searchError,
    searchLoading,
    searchTotalCount = 0,
}) => {

    const areItemsEditable = !! (
        ! searchLoading
        || searchOffset
    );
    const showMoreButton = (
        ! searchLoading
        && searchItems.length
        && searchItems.length < searchTotalCount
    );
    const showBottom = (
        showMoreButton
        || !searchItems.length
        || searchError
        || searchLoading
    );

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
                                { searchError && <Error justIcon message={ searchError } /> }
                                { searchLoading && <Loading /> }
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
                        onCancel={ E.lexicon.onCancelNew }
                        onSave={ E.lexicon.onSaveNew }
                    />
                </tbody>
            }
            { !! searchItems.length &&
                <tbody
                    key="items"
                    className={ classnames( S.items, areItemsEditable && S.areItemsEditable ) }
                >
                    { searchItems.map( item =>
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
                            { searchError && <Error message={ searchError } /> }
                            { searchLoading
                                ? <Loading />
                            : showMoreButton
                                ? <a
                                    children="🔽 Show more results"
                                    className={ classnames( S.more, QA.LEXICON_MORE ) }
                                    onClick={ onFetchMore }
                                />
                            : ! searchItems.length
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
};
