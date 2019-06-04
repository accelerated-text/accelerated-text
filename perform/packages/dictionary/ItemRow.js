import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { composeQueries }       from '../graphql/';

import { openDictionaryItem }   from '../accelerated-text/graphql';
import { orgDictionaryItem }    from '../graphql/queries.graphql';
import ShowPhrases              from '../lexicon/ShowPhrases';

import S                        from './ItemRow.sass';


export default composeQueries({
    orgDictionaryItem:      [ orgDictionaryItem, {
        id:                 'id',
    }],
    openDictionaryItem:     [ openDictionaryItem, {
        itemId:             [ 'orgDictionaryItem', 'orgDictionaryItem', 'id' ],
    }],
})(({
    openDictionaryItem,
    orgDictionaryItem: { orgDictionaryItem },
}) =>
    orgDictionaryItem &&
        <tr className={ S.className }>
            <td className={ S.dragInBlock }>
                <DragInBlock
                    fields={{ name: orgDictionaryItem.name }}
                    type="DictionaryItem"
                    width={ 36 }
                />
            </td>
            <td className={ S.name }>{ orgDictionaryItem.name }</td>
            <td>
                <ShowPhrases
                    isEditable
                    onClick={ openDictionaryItem }
                    phrases={ orgDictionaryItem.usageModels.map(
                        m => m.phrase.text
                    ) }
                />
            </td>
        </tr>
);
