import classnames       from 'classnames';
import { h }            from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { QA }           from '../tests/constants';

import S                from './ItemTable.sass';
import ItemRow          from './ItemRow';


export default ({
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

    const isLoadingNew = (
        resultsLoading
        && ! requestOffset
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
        <table
            className={ classnames(
                S.className,
                isLoadingNew && S.isLoadingNew,
                QA.LEXICON_LIST,
            ) }
        >
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
                    className={ classnames( S.items, isLoadingNew && S.isLoadingNew ) }
                >
                    { items.map( item =>
                        <ItemRow
                            className={ QA.LEXICON_ITEM }
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
                                    children="🔽 Get more results"
                                    className={ classnames( S.more, QA.LEXICON_MORE ) }
                                    onClick={ E.lexicon.onClickMore }
                                />
                            : !items.length
                                ? <Info message="No results found" />
                                : null
                            }
                        </td>
                    </tr>
                </tbody>
            }
        </table>
    );
};
