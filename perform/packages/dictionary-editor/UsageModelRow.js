import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { QA }               from '../tests/constants';
import {
    updatePhraseUsageModelDefault,
    updateReaderFlagUsage,
}   from '../graphql/mutations.graphql';
import UsageTd              from '../usage/UsageTd';


export default composeQueries({
    updatePhraseUsageModelDefault,
    updateReaderFlagUsage,
})( class DictionaryEditorUsageModelRow extends Component {

    static propTypes = {
        className:                      PropTypes.string,
        model:                          PropTypes.object,
        updatePhraseUsageModelDefault:  PropTypes.func,
        updateReaderFlagUsage:          PropTypes.func,
    };

    onChangeDefaultUsage = defaultUsage => {
        const { id } =      this.props.model;

        this.props.updatePhraseUsageModelDefault({
            variables: {
                id,
                defaultUsage,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updatePhraseUsageModelDefault: {
                    __typename:     'PhraseUsageModel',
                    id,
                    defaultUsage,
                },
            },
        });
    };

    onChangeFlagUsage = id => usage =>
        this.props.updateReaderFlagUsage({
            variables: {
                id,
                usage,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updateReaderFlagUsage: {
                    __typename:     'ReaderFlagUsage',
                    id,
                    usage,
                },
            },
        });

    render({
        className,
        model,
    }) {
        return (
            <tr className={ classnames( QA.DICT_ITEM_EDITOR_PHRASE, className ) }>
                <td className={ QA.DICT_ITEM_EDITOR_PHRASE_TEXT }>
                    { model.phrase }
                </td>
                <UsageTd
                    className={ QA.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE }
                    defaultUsage
                    onChange={ this.onChangeDefaultUsage }
                    usage={ model.defaultUsage }
                />
                { model.readerUsage.map( flagUsage =>
                    <UsageTd
                        className={ QA.DICT_ITEM_EDITOR_PHRASE_READER_USAGE }
                        key={ flagUsage.id }
                        onChange={ this.onChangeFlagUsage( flagUsage.id ) }
                        usage={ flagUsage.usage }
                    />
                )}
            </tr>
        );
    }
});
