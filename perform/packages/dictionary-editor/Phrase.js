import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import InlineEditor         from '../inline-editor/InlineEditor';
import { QA }               from '../tests/constants';
import sortFlagUsage        from '../dictionary/sort-reader-flag-usage';
import {
    deletePhrase,
    updatePhrase,
    updatePhraseDefaultUsage,
    updateReaderFlagUsage,
}   from '../graphql/mutations.graphql';
import UsageTd              from '../usage/UsageTd';


export default composeQueries({
    deletePhrase,
    updatePhrase,
    updatePhraseDefaultUsage,
    updateReaderFlagUsage,
})( class DictionaryEditorPhrase extends Component {

    static propTypes = {
        className:                  PropTypes.string,
        deletePhrase:               PropTypes.func,
        phrase:                     PropTypes.object,
        updatePhrase:               PropTypes.func,
        updatePhraseDefaultUsage:   PropTypes.func,
        updateReaderFlagUsage:      PropTypes.func,
    };

    onChangeDefaultUsage = defaultUsage => {
        const { id } =      this.props.phrase;

        this.props.updatePhraseDefaultUsage({
            variables: {
                id,
                defaultUsage,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updatePhraseDefaultUsage: {
                    ...this.props.phrase,
                    defaultUsage,
                },
            },
        });
    };

    onChangeFlagUsage = flagUsage => usage => {
        const { id } =      flagUsage;

        this.props.updateReaderFlagUsage({
            variables: {
                id,
                usage,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updateReaderFlagUsage: {
                    ...flagUsage,
                    usage,
                },
            },
        });
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
        className,
        phrase,
    }) {
        return (
            <tr className={ classnames( QA.DICT_ITEM_EDITOR_PHRASE, className ) }>
                <td className={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT }>
                    <InlineEditor
                        compact
                        onDelete={ this.onDeletePhrase }
                        onSubmit={ this.onChangePhraseText }
                        text={ phrase.text }
                    />
                </td>
                <UsageTd
                    className={ QA.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE }
                    defaultUsage
                    onChange={ this.onChangeDefaultUsage }
                    usage={ phrase.defaultUsage }
                />
                { sortFlagUsage( phrase.readerFlagUsage ).map( flagUsage =>
                    <UsageTd
                        className={ QA.DICT_ITEM_EDITOR_PHRASE_RFLAG_USAGE }
                        key={ flagUsage.id }
                        onChange={ this.onChangeFlagUsage( flagUsage ) }
                        usage={ flagUsage.usage }
                    />
                )}
            </tr>
        );
    }
});
