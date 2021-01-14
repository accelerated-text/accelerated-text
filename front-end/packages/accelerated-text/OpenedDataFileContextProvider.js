import { h, Component }     from 'preact';
import { path }             from 'ramda';

import { getRelevantSamples }      from '../graphql/queries.graphql';
import { Query }            from '../graphql/';

import Context              from './OpenedDataFileContext';
import OpenedPlanContext    from './OpenedPlanContext';


export default class OpenedDataFileContextProvider extends Component {

    static contextType =    OpenedPlanContext;

    value =                 {};

    render({ children }, _, openedPlan ) {
        const id =          path([ 'plan', 'dataSampleId' ], openedPlan );
        const method =      path([ 'plan', 'dataSampleMethod'], openedPlan );
        return (
            <Query
                query={ getRelevantSamples }
                skip={ ! id }
                variables={{ id, method }}
            >
                { ({ error, data, loading }) =>
                    <Context.Provider
                        children={ children }
                        value={ Object.assign( this.value, {
                            file:       data && data.getRelevantSamples,
                            error,
                            loading,
                        }) }
                    />
                }
            </Query>
        );
    }
}
