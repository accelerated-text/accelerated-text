import classnames           from 'classnames';
import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';
import { Error, Loading }   from '../ui-messages';
import { mount, useStores } from '../vesa/';

import EditLines            from './EditLines';
import lexiconItem          from './item-store';
import lexiconItemAdapter   from './item-adapter';
import S                    from './ItemRow.sass';


export default mount(
    { lexiconItem },
    [ lexiconItemAdapter ],
)( useStores([
    'lexiconItem',
])(({
    className,
    E,
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
                { ! showEdit &&
                    <DragInBlock
                        color={ S.dragInColor }
                        comment={ item.synonyms.join( '\n' ) }
                        fields={{ text: item.key }}
                        type="Lexicon"
                        width={ 36 }
                    />
                }
            </td>
            <td className={ S.itemId }>
                { item.key }
            </td>
            <td className={ S.phrases }>
                { showEdit
                    ? <EditLines
                        lines={ item.synonyms }
                        onClickCancel={ E.lexiconItem.onCancelEdit }
                        onClickSave={ E.lexiconItem.onSave }
                        saving={ saving }
                        status={
                            error
                                ? <Error message={ error } />
                            : saving
                                ? <Loading message="Saving..." />
                            : null
                        }
                    />
                    : <div className={ S.showPhrases } onClick={ E.lexiconItem.onClickEdit }>
                        { item.synonyms.map( phrase =>
                            <span className={ S.phrase }>{ phrase }</span>
                        )}
                        <span className={ S.editIcon }> 📝</span>
                    </div>
                }
            </td>
        </tr>
    );
}));
