import { h }            from 'preact';

import S                from './ItemList.sass';
import ItemRow          from './ItemRow';


export default ({ items }) =>
    <div className={ S.className } >
        <div className={ S.headerRow }>
            <div>ID</div>
            <div>words</div>
        </div>
        { items.map( item =>
            <ItemRow
                className={ S.itemRow }
                item={ item }
                key={ item.key }
            />
        )}
    </div>;
