import classnames           from 'classnames';
import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';
import { mount, useStores } from '../vesa/';
import { QA }               from '../tests/constants';

import EditPhrases          from './EditPhrases';
import lexiconItem          from './item-store';
import lexiconItemAdapter   from './item-adapter';
import S                    from './ItemRow.sass';
import ShowPhrases          from './ShowPhrases';


export default mount(
    { lexiconItem },
    [ lexiconItemAdapter ],
)( useStores([
    'lexiconItem',
])(({
    className,
    E,
    isEditable,
    lexiconItem: {
        editing,
        error,
        item,
        saving,
    },
}) => {
    const showEdit = editing || error || saving;

    return (
        <tr className={ classnames( S.className, className ) }>
            <td className={ S.dragInBlock }>
                { item.key &&
                    <DragInBlock
                        className={ QA.LEXICON_ITEM_BLOCK }
                        color={ S.dragInColor }
                        comment={ item.synonyms.join( '\n' ) }
                        fields={{ text: item.key }}
                        type="Lexicon"
                        width={ 36 }
                    />
                }
            </td>
            <td className={ classnames( S.itemId, QA.LEXICON_ITEM_ID ) }>
                { item.key }
            </td>
            <td className={ S.phrases }>
                { showEdit
                    ? <EditPhrases
                        phrases={ item.synonyms }
                        onClickCancel={ E.lexiconItem.onCancelEdit }
                        onClickSave={ E.lexiconItem.onSave }
                        status={{ editing, error, saving }}
                    />
                    : <ShowPhrases
                        className={ QA.LEXICON_ITEM_PHRASES }
                        isEditable={ isEditable }
                        onClick={ isEditable && E.lexiconItem.onClickEdit }
                        phrases={ item.synonyms }
                    />
                }
            </td>
        </tr>
    );
}));
