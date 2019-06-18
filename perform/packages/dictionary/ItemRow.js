import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import { openDictionaryItem }   from '../accelerated-text/local-state';

import S                        from './ItemRow.sass';
import ShowPhrases              from './ShowPhrases';


export default composeQueries({
    openDictionaryItem: [ openDictionaryItem, {
        itemId:                 [ 'item', 'id' ],
    }],
})(({
    item,
    openDictionaryItem,
}) =>
    <tr className={ S.className }>
        <td className={ S.dragInBlock }>
            <DragInBlock
                color={ S.dragInColor }
                fields={{ name: item.name }}
                type={ DictionaryItemBlock.type }
                width={ 36 }
            />
        </td>
        <td
            children={ item.name }
            className={ S.name }
            onClick={ openDictionaryItem }
        />
        <td onClick={ openDictionaryItem }>
            <ShowPhrases
                phrases={ item.phrases.map(
                    phrase => phrase.text
                ) }
            />
        </td>
    </tr>
);
