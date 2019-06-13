import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import { dictionaryItem }       from '../graphql/queries.graphql';
import { openDictionaryItem }   from '../accelerated-text/local-state';
import ShowPhrases              from '../lexicon/ShowPhrases';

import S                        from './ItemRow.sass';


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
    dictionaryItem &&
        <tr className={ S.className }>
            <td className={ S.dragInBlock }>
                <DragInBlock
                    color={ S.dragInColor }
                    fields={{ name: dictionaryItem.name }}
                    type={ DictionaryItemBlock.type }
                    width={ 36 }
                />
            </td>
            <td
                children={ dictionaryItem.name }
                className={ S.name }
                onClick={ openDictionaryItem }
            />
            <td onClick={ openDictionaryItem }>
                <ShowPhrases
                    phrases={ dictionaryItem.usageModels.map(
                        m => m.phrase
                    ) }
                />
            </td>
        </tr>
);
