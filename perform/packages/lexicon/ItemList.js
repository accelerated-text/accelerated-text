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
    <div className={ classnames( S.className, QA.LEXICON_LIST ) } >
        <div className={ S.headerRow }>
            <div>ID</div>
            <div>words</div>
        </div>
        { newItem &&
            <ItemRow
                className={ classnames( S.itemRow, QA.LEXICON_NEW_ITEM ) }
                editing={ !newItemSaved }
                item={ newItem }
                key={ newItem.key || 'NEW ITEM' }
                onCancel={ onCancelNew }
                onSave={ onSaveNew }
            />
        }
        { items.map( item =>
            <ItemRow
                className={ classnames( S.itemRow, QA.LEXICON_ITEM ) }
                item={ item }
                key={ item.key }
            />
        )}
    </div>;
