import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
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

    onChangePhraseText = text => {
        const { id } =      this.props.phrase;

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
        });
    };

    onDeletePhrase = () => {
        const { id } =      this.props.phrase;

        this.props.deletePhrase({
            variables:              { id },
        });
    };

    render({
        phrase,
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
            />
        );
    }
});
