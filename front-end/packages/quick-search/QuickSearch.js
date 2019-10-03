import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import composeContexts      from '../compose-contexts/';
import {
    dictionary,
    searchThesaurus,
}                           from '../graphql/queries.graphql';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import { Query }            from '../graphql/';
import WorkspaceContext     from '../workspace-context/WorkspaceContext';

import Form                 from './Form';
import getResults           from './get-results';


export default composeContexts({
    openedFile:             OpenedFileContext,
    openedPlan:             OpenedPlanContext,
    workspace:              WorkspaceContext,
})( class QuickSearch extends Component {

    static propTypes = {
        filterTypes:        PropTypes.func,
        onChooseResult:     PropTypes.func,
        openedFile:         PropTypes.object.isRequired,
        openedPlan:         PropTypes.object.isRequired,
        sortTypes:          PropTypes.func,
    };

    state = {
        query:              '',
    };

    onChangeQuery = query => {
        this.setState({ query });
    };

    render = ({
        filterTypes,
        onChooseResult,
        openedFile: { file },
        openedPlan: { plan },
        sortTypes,
        workspace:  { workspace },
    }, {
        query,
    }) =>
        <Query query={ dictionary }>
            { ({
                error:              dictionaryError,
                loading:            dictionaryLoading,
                data:               { dictionary },
            }) =>
                <Query query={ searchThesaurus } variables={{ query }}>
                    { ({
                        error:      thesaurusError,
                        data:       { searchThesaurus },
                        loading:    thesaurusLoading,
                    }) =>
                        <Form
                            autofocus
                            error={ dictionaryError || thesaurusError }
                            loading={ dictionaryLoading || thesaurusLoading }
                            results={ getResults({
                                dictionary,
                                file,
                                filterTypes,
                                plan,
                                query,
                                searchThesaurus,
                                sortTypes,
                                workspace,
                            }) }
                            onChangeQuery={ this.onChangeQuery }
                            onChooseResult={ onChooseResult }
                            query={ query }
                        />
                    }
                </Query>
            }
        </Query>;
});
