import { h }            from 'preact';

import S                from './ItemList.sass';
import ItemRow          from './ItemRow';


export default ({
    items,
    newItem,
    newItemSaved,
    onCancelNew,
    onSaveNew,
}) =>
    <div className={ S.className } >
        <div className={ S.headerRow }>
            <div>ID</div>
            <div>words</div>
        </div>
        { newItem &&
            <ItemRow
                className={ S.itemRow }
                editing={ !newItemSaved }
                item={ newItem }
                key={ newItem.key || 'NEW ITEM' }
                onCancel={ onCancelNew }
                onSave={ onSaveNew }
            />
        }
        { items.map( item =>
            <ItemRow
                className={ S.itemRow }
                item={ item }
                key={ item.key }
            />
        )}
    </div>;
