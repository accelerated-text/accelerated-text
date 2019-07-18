import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { QA }               from '../tests/constants';
import {
    updateReaderFlagUsage,
}   from '../graphql/mutations.graphql';
import UsageTd              from '../usage/UsageTd';


export default composeQueries({
    updateReaderFlagUsage,
})( class DictionaryEditorPhraseReaderFlagUsage extends Component {

    static propTypes = {
        flagUsage:                  PropTypes.object.isRequired,
        updateReaderFlagUsage:      PropTypes.func.isRequired,
    };

    state = {
        error:                      null,
        loading:                    false,
    };

    onChange = usage => {
        const { flagUsage } =       this.props;

        this.setState({
            loading:                true,
        });

        this.props.updateReaderFlagUsage({
            variables: {
                id:                 flagUsage.id,
                usage,
            },
            optimisticResponse: {
                __typename:         'Mutation',
                updateReaderFlagUsage: {
                    ...flagUsage,
                    usage,
                },
            },
        }).then( mutationResult =>
            this.setState({
                error:              mutationResult.error,
                loading:            false,
            })
        );
    };

    render({
        flagUsage,
    }, {
        error,
        loading,
    }) {
        return (
            <UsageTd
                className={ QA.DICT_ITEM_EDITOR_PHRASE_RFLAG_USAGE }
                error={ error }
                loading={ loading }
                onChange={ this.onChange }
                usage={ flagUsage.usage }
            />
        );
    }
});
