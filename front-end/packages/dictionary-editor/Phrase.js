import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { Error }            from '../ui-messages/';
import { QA }               from '../tests/constants';
import sortFlagUsage        from '../dictionary/sort-reader-flag-usage';
import {
    updatePhraseDefaultUsage,
}   from '../graphql/mutations.graphql';
import UsageTd              from '../usage/UsageTd';

import FlagUsage            from './PhraseReaderFlagUsage';
import PhraseText           from './PhraseText';


export default composeQueries({
    updatePhraseDefaultUsage,
})( class DictionaryEditorPhrase extends Component {

    static propTypes = {
        className:                  PropTypes.string,
        phrase:                     PropTypes.object.isRequired,
        updatePhraseDefaultUsage:   PropTypes.func.isRequired,
    };

    state = {
        upDefaultError:             null,
        upDefaultLoading:           false,
    };

    onChangeDefaultUsage = defaultUsage => {
        const { id } =      this.props.phrase;

        this.setState({
            upDefaultLoading:       true,
        });

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
        }).then( mutationResult =>
            this.setState({
                upDefaultError:     mutationResult.error,
                upDefaultLoading:   false,
            })
        );
    };

    render({
        className,
        phrase,
        readerFlags,
    }, {
        upDefaultError,
        upDefaultLoading,
    }) {
        return (
            <tr className={ classnames( QA.DICT_ITEM_EDITOR_PHRASE, className ) }>
                <td>
                    <PhraseText phrase={ phrase } />
                </td>
                <UsageTd
                    className={ QA.DICT_ITEM_EDITOR_PHRASE_DEFAULT_USAGE }
                    defaultUsage
                    error={ upDefaultError }
                    loading={ upDefaultLoading }
                    onChange={ this.onChangeDefaultUsage }
                    usage={ phrase.defaultUsage }
                />
                { sortFlagUsage( readerFlags, phrase.readerFlagUsage )
                    .map( flagUsage =>
                        flagUsage
                            ? <FlagUsage key={ flagUsage.id } flagUsage={ flagUsage } />
                            : <td key={ flagUsage.id }>
                                <Error
                                    justIcon
                                    message="No usage data found for the flag. Try reloading the app."
                                />
                            </td>
                    )
                }
            </tr>
        );
    }
});
