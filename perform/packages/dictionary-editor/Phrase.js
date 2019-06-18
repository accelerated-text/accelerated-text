import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import {
    updatePhraseDefaultUsage,
    updateReaderFlagUsage,
}   from '../graphql/mutations.graphql';
import UsageTd              from '../usage/UsageTd';


export default composeQueries({
    updatePhraseDefaultUsage,
    updateReaderFlagUsage,
})( class DictionaryEditorPhrase extends Component {

    static propTypes = {
        className:                  PropTypes.string,
        phrase:                     PropTypes.object,
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
                    __typename:     'Phrase',
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
        phrase,
    }) {
        return (
            <tr className={ className }>
                <td>{ phrase.text }</td>
                <UsageTd
                    defaultUsage
                    onChange={ this.onChangeDefaultUsage }
                    usage={ phrase.defaultUsage }
                />
                { phrase.readerFlagUsage.map( flagUsage =>
                    <UsageTd
                        key={ flagUsage.id }
                        onChange={ this.onChangeFlagUsage( flagUsage.id ) }
                        usage={ flagUsage.usage }
                    />
                )}
            </tr>
        );
    }
});
