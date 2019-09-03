import { h, Component }     from 'preact';

import getOpenedPlan        from '../plan-list/get-opened-plan';
import { getDataFile }      from '../graphql/queries.graphql';
import { getStatus }        from '../document-plans/functions';
import { Query }            from '../graphql/';
import { useStores }        from '../vesa/';

import Context              from './Context';


export default useStores([
    'documentPlans',
    'planList',
])( class DocumentPlansContextProvider extends Component {

    value =                 {};

    render({ children, ...props }) {

        const openedPlan =  getOpenedPlan( props );
        const openedPlanStatus =
            openedPlan
                ? getStatus( props.documentPlans, openedPlan )
                : null;

        const queryVariables =
            ( openedPlan && openedPlan.dataSampleId )
                ? { id:     openedPlan.dataSampleId }
                : null;

        return (
            <Query
                query={ getDataFile }
                skip={ ! queryVariables }
                variables={ queryVariables }
            >
                { ({ error, data, loading }) =>
                    <Context.Provider
                        children={ children }
                        value={ Object.assign( this.value, {
                            openedDataFile:         data ? data.getDataFile : null,
                            openedDataFileError:    error,
                            openedDataFileLoading:  loading,
                            openedPlan,
                            openedPlanStatus,
                        }) }
                    />
                }
            </Query>
        );
    }
});
