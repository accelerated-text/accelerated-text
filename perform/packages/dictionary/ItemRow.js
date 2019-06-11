import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import { openDictionaryItem }   from '../accelerated-text/graphql';
import { dictionaryItem }       from '../graphql/queries.graphql';
import ShowPhrases              from '../lexicon/ShowPhrases';

import S                        from './ItemRow.sass';


export default composeQueries({
    dictionaryItem:      [ dictionaryItem, {
        id:                 'id',
    }],
    openDictionaryItem:     [ openDictionaryItem, {
        itemId:             [ 'dictionaryItem', 'dictionaryItem', 'id' ],
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
                    type="Dictionary-item"
                    width={ 36 }
                />
            </td>
            <td className={ S.name }>{ dictionaryItem.name }</td>
            <td>
                <ShowPhrases
                    isEditable
                    onClick={ openDictionaryItem }
                    phrases={ dictionaryItem.usageModels.map(
                        m => m.phrase
                    ) }
                />
            </td>
        </tr>
);
