import classnames               from 'classnames';
import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import { dictionaryItem }       from '../graphql/queries.graphql';
import { openDictionaryItem }   from '../accelerated-text/local-state';
import { QA }                   from '../tests/constants';

import S                        from './ItemRow.sass';
import ShowPhrases              from './ShowPhrases';


export default composeQueries({
    dictionaryItem: [ dictionaryItem, {
        id:                     'id',
    }],
    openDictionaryItem: [ openDictionaryItem, {
        itemId:                 [ 'dictionaryItem', 'dictionaryItem', 'id' ],
    }],
})(({
    openDictionaryItem,
    dictionaryItem: { dictionaryItem },
}) =>
    <tr className={ classnames( S.className, QA.DICTIONARY_ITEM ) }>
        <td className={ S.dragInBlock }>
            { dictionaryItem &&
                <DragInBlock
                    color={ S.dragInColor }
                    fields={{ name: dictionaryItem.name }}
                    type={ DictionaryItemBlock.type }
                    width={ 36 }
                />
            }
        </td>
        <td
            children={ dictionaryItem ? dictionaryItem.name : '' }
            className={ classnames( S.name, QA.DICTIONARY_ITEM_NAME ) }
            onClick={ openDictionaryItem }
        />
        <td className={ QA.DICTIONARY_ITEM_PHRASES } onClick={ openDictionaryItem }>
            { dictionaryItem &&
                <ShowPhrases
                    phrases={ dictionaryItem.usageModels.map(
                        m => m.phrase
                    ) }
                />
            }
        </td>
    </tr>
);
