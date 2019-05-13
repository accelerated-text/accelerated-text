import classnames       from 'classnames';
import { h }            from 'preact';

import { QA }           from '../tests/constants';

import S                from './ItemList.sass';
import ItemRow          from './ItemRow';


export default ({
    items,
    newItem,
    newItemSaved,
    onCancelNew,
    onSaveNew,
}) =>
    <table className={ classnames( S.className, QA.LEXICON_LIST ) }>
        <thead>
            <tr>
                <th />
                <th>ID</th>
                <th>Phrases</th>
            </tr>
        </thead>
        <tbody>
            { newItem &&
                <ItemRow
                    className={ classnames( S.newItemRow, QA.LEXICON_NEW_ITEM ) }
                    editing={ !newItemSaved }
                    item={ newItem }
                    key={ newItem.key || 'NEW ITEM' }
                    onCancel={ onCancelNew }
                    onSave={ onSaveNew }
                />
            }
            { items.map( item =>
                <ItemRow
                    className={ QA.LEXICON_ITEM }
                    item={ item }
                    key={ item.key }
                />
            )}
        </tbody>
    </table>;
