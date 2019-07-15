import classnames               from 'classnames';
import { h, Component }         from 'preact';

import { composeQueries }       from '../graphql/';
import InlineEditor             from '../inline-editor/InlineEditor';
import { QA }                   from '../tests/constants';
import { updateDictionaryItem } from '../graphql/mutations.graphql';

import S                        from './Title.sass';


export default composeQueries({
    updateDictionaryItem,
})( class DictionaryEditorTitle extends Component {

    onSubmit = name =>
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
        });

    render({ className, item }) {
        return (
            <InlineEditor
                cancelClassName={ QA.DICT_ITEM_EDITOR_NAME_CANCEL }
                className={ classnames( S.className, className ) }
                inputClassName={ QA.DICT_ITEM_EDITOR_NAME_INPUT }
                onSubmit={ this.onSubmit }
                saveClassName={ QA.DICT_ITEM_EDITOR_NAME_SAVE }
                text={ item.name }
                textClassName={ classnames( S.text, QA.DICT_ITEM_EDITOR_NAME ) }
            />
        );
    }
});

