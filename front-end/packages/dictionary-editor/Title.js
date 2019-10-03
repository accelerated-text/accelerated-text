import classnames               from 'classnames';
import { h, Component }         from 'preact';

import { composeQueries }       from '../graphql/';
import { Error, Loading }       from '../ui-messages/';
import InlineEditor             from '../inline-editor/InlineEditor';
import { QA }                   from '../tests/constants';
import SmallPOS                 from '../part-of-speech/SmallView';
import { updateDictionaryItem } from '../graphql/mutations.graphql';

import S                        from './Title.sass';


export default composeQueries({
    updateDictionaryItem,
})( class DictionaryEditorTitle extends Component {

    state = {
        updateError:            null,
        updateLoading:          false,
    };

    onSubmit = name => {
        this.setState({
            updateLoading:      true,
        });

        this.props.updateDictionaryItem({
            optimisticResponse: {
                __typename:     'Mutation',
                updateDictionaryItem: {
                    ...this.props.item,
                    name,
                },
            },
            variables: {
                id:             this.props.item.id,
                name,
            },
        }).then( mutationResult =>
            this.setState({
                updateError:    mutationResult.error,
                updateLoading:  false,
            })
        );
    };

    render({ className, item }, { updateError, updateLoading }) {
        return (
            <InlineEditor
                cancelClassName={ QA.DICT_ITEM_EDITOR_NAME_CANCEL }
                className={ classnames( S.className, className ) }
                inputClassName={ QA.DICT_ITEM_EDITOR_NAME_INPUT }
                onSubmit={ this.onSubmit }
                saveClassName={ QA.DICT_ITEM_EDITOR_NAME_SAVE }
                text={ item.name }
                textClassName={ classnames( S.text, QA.DICT_ITEM_EDITOR_NAME ) }
            >
                <SmallPOS className={ S.pos } partOfSpeech={ item.partOfSpeech } />
                { updateError
                    ? <Error message={ updateError } />
                : updateLoading
                    ? <Loading justIcon />
                    : null
                }
            </InlineEditor>
        );
    }
});

