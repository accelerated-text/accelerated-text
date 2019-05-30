import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';
import ShowPhrases          from '../lexicon/ShowPhrases';

import S                    from './DictionaryItemRow.sass';


export default ({ item }) =>
    <tr className={ S.className } key={ item.id }>
        <td className={ S.dragInBlock }>
            <DragInBlock
                fields={{ name: item.name }}
                type="DictionaryItem"
                width={ 36 }
            />
        </td>
        <td className={ S.name }>{ item.name }</td>
        <td>
            <ShowPhrases
                phrases={ item.usageModels.map( m => m.phrase.text ) }
            />
        </td>
    </tr>;
