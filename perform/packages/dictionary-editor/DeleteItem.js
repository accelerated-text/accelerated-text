import classnames               from 'classnames';
import { h, Component }         from 'preact';
import PropTypes                from 'prop-types';

import { composeQueries }       from '../graphql/';
import { deleteDictionaryItem } from '../graphql/mutations.graphql';
import {
    Error,
    Loading,
    onConfirmDelete,
}                               from '../ui-messages/';
import { QA }                   from '../tests/constants';

import S                        from './DeleteItem.sass';


export default composeQueries({
    deleteDictionaryItem,
})( class DictionaryEditorDeleteItem extends Component {

    static propTypes = {
        className:              PropTypes.string,
        itemId:                 PropTypes.string.isRequired,
        onDelete:               PropTypes.func,
    };

    state = {
        deleteError:            null,
        deleteLoading:          false,
    };

    onClick = () => {
        this.setState({
            deleteLoading:      true,
        });

        onConfirmDelete(() =>
            this.props.deleteDictionaryItem({
                refetchQueries:     [ 'dictionary' ],
                variables: {
                    id:             this.props.itemId,
                },
            }).then( mutationResult => {
                this.setState({
                    deleteError:    mutationResult.error,
                    deleteLoading:  false,
                });
                this.props.onDelete();
            })
        );
    };

    render({ className }, { deleteError, deleteLoading }) {
        return (
            <button
                children="🗑️ Delete this item"
                className={ classnames( S.className, className, QA.DICT_ITEM_EDITOR_DELETE ) }
                disabled={ deleteLoading }
                onClick={ this.onClick }
            >
                { deleteLoading
                    ? <Loading message="Deleting item..." />
                    : '🗑️ Delete this item'
                }
                { deleteError
                    && <Error message={ deleteError } />
                }
            </button>
        );
    }
});
