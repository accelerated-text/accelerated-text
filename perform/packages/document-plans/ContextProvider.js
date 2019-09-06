import { h, Component }     from 'preact';
import { path }             from 'ramda';

import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import { getDataFile }      from '../graphql/queries.graphql';
import { Query }            from '../graphql/';

import Context              from './Context';


const getDataSampleId =     path([ 'openedPlan', 'dataSampleId' ]);


export default class DocumentPlansContextProvider extends Component {

    static contextType =    OpenedPlanContext;

    value =                 {};

    render = ({ children }, _, context ) =>
        <Query
            query={ getDataFile }
            skip={ ! getDataSampleId( context ) }
            variables={{
                id:         getDataSampleId( context ),
            }}
        >
            { ({ error, data, loading }) =>
                <Context.Provider
                    children={ children }
                    value={ Object.assign( this.value, {
                        ...context,
                        openedDataFile:         data && data.getDataFile,
                        openedDataFileError:    error,
                        openedDataFileLoading:  loading,
                    }) }
                />
            }
        </Query>;
}
