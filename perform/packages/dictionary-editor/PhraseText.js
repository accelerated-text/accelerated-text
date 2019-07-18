import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { Error, Loading }   from '../ui-messages/';
import InlineEditor         from '../inline-editor/InlineEditor';
import { QA }               from '../tests/constants';
import {
    deletePhrase,
    updatePhrase,
}   from '../graphql/mutations.graphql';


export default composeQueries({
    deletePhrase,
    updatePhrase,
})( class DictionaryEditorPhraseText extends Component {

    static propTypes = {
        deletePhrase:               PropTypes.func.isRequired,
        phrase:                     PropTypes.object.isRequired,
        updatePhrase:               PropTypes.func.isRequired,
    };

    state = {
        deleteError:                null,
        deleteLoading:              false,
        updateError:                null,
        updateLoading:              false,
    };

    onChangePhraseText = text => {
        const { id } =      this.props.phrase;

        this.setState({
            updateLoading:          true,
        });

        this.props.updatePhrase({
            variables: {
                id,
                text,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updatePhrase: {
                    ...this.props.phrase,
                    text,
                },
            },
        }).then( mutationResult =>
            this.setState({
                updateError:        mutationResult.error,
                updateLoading:      false,
            })
        );
    };

    onDeletePhrase = () => {
        const { id } =      this.props.phrase;

        this.setState({
            deleteLoading:          true,
        });

        this.props.deletePhrase({
            variables:              { id },
        }).then( mutationResult =>
            this.setState({
                deleteError:        mutationResult.error,
                deleteLoading:      false,
            })
        );
    };

    render({
        phrase,
    }, {
        deleteError,
        deleteLoading,
        updateError,
        updateLoading,
    }) {
        return (
            <InlineEditor
                cancelClassName={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT_CANCEL }
                compact
                deleteClassName={ QA.DICT_ITEM_EDITOR_PHRASE_DELETE }
                inputClassName={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT_INPUT }
                onDelete={ this.onDeletePhrase }
                onSubmit={ this.onChangePhraseText }
                saveClassName={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT_SAVE }
                text={ phrase.text }
                textClassName={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT }
            >
                { deleteError
                    ? <Error justIcon message={ deleteError } />
                : updateError
                    ? <Error justIcon message={ updateError } />
                : deleteLoading
                    ? <Loading justIcon message="Deleting..." />
                : updateLoading
                    ? <Loading justIcon message="Saving..." />
                    : null
                }
            </InlineEditor>
        );
    }
});
