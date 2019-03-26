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
        this.props.E.lexiconItem.onUpdate();
    };

    render({
        className,
        E,
        lexiconItem: {
            editing,
            editText,
            error,
            item,
            loading,
        },
    }) {
        const showEdit = editing || error || loading;

        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.key }>{ item.key }</div>
                <div className={ S.words } onClick={ E.lexiconItem.onClickEdit }>
                    { showEdit
                        ? <EditWords
                            loading={ loading }
                            onClickCancel={ this.onClickCancel }
                            onClickSave={ this.onClickSave }
                            onChangeText={ E.lexiconItem.onChangeText }
                            status={
                                error
                                    ? <Error message={ error } />
                                : loading
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
