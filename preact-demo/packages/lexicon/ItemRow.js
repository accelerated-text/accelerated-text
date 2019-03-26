import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { Error, Loading }   from '../ui-messages';
import { mount, useStores } from '../vesa/';

import EditWords            from './EditWords';
import lexiconItem          from './item-store';
import lexiconItemAdapter   from './item-adapter';
import S                    from './ItemRow.sass';


export default mount(
    { lexiconItem },
    [ lexiconItemAdapter ],
)( useStores([
    'lexiconItem',
])( class LexiconItemRow extends Component {

    onClickCancel = evt => {

        evt.stopPropagation();
        this.props.E.lexiconItem.onCancelEdit();
    };

    onClickSave = evt => {

        evt.stopPropagation();
        this.props.E.lexiconItem.onSave();
    };

    render({
        className,
        E,
        lexiconItem: {
            editing,
            editText,
            error,
            item,
            saving,
        },
    }) {
        const showEdit = editing || error || saving;

        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.key }>{ item.key }</div>
                <div className={ S.words } onClick={ E.lexiconItem.onClickEdit }>
                    { showEdit
                        ? <EditWords
                            onChangeText={ E.lexiconItem.onChangeText }
                            onClickCancel={ this.onClickCancel }
                            onClickSave={ this.onClickSave }
                            saving={ saving }
                            status={
                                error
                                    ? <Error message={ error } />
                                : saving
                                    ? <Loading message="Saving..." />
                                : null
                            }
                            text={ editText }
                        />
                        : [
                            item.synonyms.join( ', ' ),
                            <span className={ S.edit }> 📝</span>,
                        ]
                    }
                </div>
            </div>
        );
    }
}));
