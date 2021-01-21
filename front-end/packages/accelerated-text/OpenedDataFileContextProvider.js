import { h, Component }     from 'preact';
import { path }             from 'ramda';

import { getDataFile }      from '../graphql/queries.graphql';
import { Query }            from '../graphql/';

import Context              from './OpenedDataFileContext';
import OpenedPlanContext    from './OpenedPlanContext';


export default class OpenedDataFileContextProvider extends Component {

    static contextType =    OpenedPlanContext;

    value =                 {};

    render({ children }, _, openedPlan ) {
        const id =          path([ 'plan', 'dataSampleId' ], openedPlan );
        return (
            <Query
                query={ getDataFile }
                skip={ ! id }
                variables={{ id }}
            >
                { ({ error, data, loading }) =>
                    <Context.Provider
                        children={ children }
                        value={ Object.assign( this.value, {
                            file:       data && data.getDataFile,
                            error,
                            loading,
                        }) }
                    />
                }
            </Query>
        );
    }
}
