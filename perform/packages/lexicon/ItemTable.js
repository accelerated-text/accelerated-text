import classnames           from 'classnames';
import { h }                from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { QA }               from '../tests/constants';
import { useStores }        from '../vesa/';

import S                    from './ItemTable.sass';
import ItemRow              from './ItemRow';


export default useStores([
    'lexicon',
])(({
    E,
    lexicon: {
        items,
        newItem,
        newItemSaved,
        requestOffset,
        resultsError,
        resultsLoading,
        totalCount,
    },
}) => {

    const areItemsEditable = !! (
        ! resultsLoading
        || requestOffset
    );
    const showMoreButton = (
        ! resultsLoading
        && items.length
        && items.length < totalCount
    );
    const showBottom = (
        showMoreButton
        || !items.length
        || resultsError
        || resultsLoading
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
                                { resultsError && <Error justIcon message={ resultsError } /> }
                                { resultsLoading && <Loading /> }
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
            { !! items.length &&
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
                            { resultsError && <Error message={ resultsError } /> }
                            { resultsLoading
                                ? <Loading />
                            : showMoreButton
                                ? <a
                                    children="🔽 Show more results"
                                    className={ classnames( S.more, QA.LEXICON_MORE ) }
                                    onClick={ E.lexicon.onClickMore }
                                />
                            : !items.length
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
});
