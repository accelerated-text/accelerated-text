import classnames           from 'classnames';
import { h }                from 'preact';

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
        <div className={ classnames( S.className, className ) }>
            <div className={ S.key }>{ item.key }</div>
            { showEdit
                ? <div className={ S.words } key="edit">
                    <EditLines
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
                </div>
                : <div className={ S.words } key="show" onClick={ E.lexiconItem.onClickEdit }>
                    { item.synonyms.join( ', ' ) }
                    <span className={ S.edit }> 📝</span>
                </div>
            }
        </div>
    );
}));
