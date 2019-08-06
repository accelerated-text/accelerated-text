import classnames               from 'classnames';
import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import DictionaryItemModifier   from '../nlg-blocks/Dictionary-item-modifier';
import { openDictionaryItem }   from '../accelerated-text/local-state';
import { QA }                   from '../tests/constants';

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
    <tr className={ classnames( S.className, QA.DICTIONARY_ITEM ) }>
        <td className={ S.dragInBlock }>
            { item &&
                <DragInBlock
                    color={ S.itemBlockColor }
                    mutation={{
                        id:     item.id,
                        name:   item.name,
                    }}
                    type={ DictionaryItemBlock.type }
                    width={ 36 }
                />
            }
        </td>
        <td className={ S.dragInBlock }>
            { item &&
                <DragInBlock
                    color={ S.itemModifierColor }
                    mutation={{
                        id:     item.id,
                        name:   item.name,
                    }}
                    type={ DictionaryItemModifier.type }
                    width={ 36 }
                />
            }
        </td>
        <td
            children={ item ? item.name : '' }
            className={ classnames( S.name, QA.DICTIONARY_ITEM_NAME ) }
            onClick={ openDictionaryItem }
        />
        <td className={ QA.DICTIONARY_ITEM_PHRASES } onClick={ openDictionaryItem }>
            { item &&
                <ShowPhrases
                    phrases={
                        item.phrases
                            .map( phrase => phrase.text )
                            .sort()
                    }
                />
            }
        </td>
    </tr>
);
