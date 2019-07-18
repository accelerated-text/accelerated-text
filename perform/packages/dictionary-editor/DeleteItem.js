import classnames               from 'classnames';
import { h, Component }         from 'preact';
import PropTypes                from 'prop-types';

import { composeQueries }       from '../graphql/';
import { deleteDictionaryItem } from '../graphql/mutations.graphql';
import { onConfirmDelete }      from '../ui-messages/';
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

    onClick = () =>
        onConfirmDelete(() =>
            this.props.deleteDictionaryItem({
                refetchQueries:     [ 'dictionary' ],
                variables: {
                    id:             this.props.itemId,
                },
            }).then( this.props.onDelete )
        );

    render({ className }) {
        return (
            <button
                children="🗑️ Delete this item"
                className={ classnames( S.className, className, QA.DICT_ITEM_EDITOR_DELETE ) }
                onClick={ this.onClick }
            />
        );
    }
});
