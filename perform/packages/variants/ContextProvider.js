import { h, Component }     from 'preact';

import DocumentPlansContext from '../document-plans/Context';
import { getPlanDataRecord }    from '../data-samples/functions';

import Context              from './Context';
import { getVariants }      from './api';


const canGetResult = ({ openedPlan, openedDataFile }) => (
    getPlanDataRecord( openedDataFile, openedPlan )
    && openedPlan.id
    && openedPlan.updatedAt
);


const getResultKey = ({ openedPlan = {}, openedDataFile }) =>
    JSON.stringify({
        planId:             openedPlan.id,
        planUpdatedAt:      openedPlan.updatedAt,
        /// TODO: reader-flags
    });


export default class VariantsContextProvider extends Component {

    static contextType =    DocumentPlansContext;

    state = {
        error:              null,
        loading:            false,
        result:             null,
        resultKey:          null,
    };

    onUpdates = () => {
        if( canGetResult( this.context )) {
            const resultKey =   getResultKey( this.context );
            if( this.state.resultKey !== resultKey ) {
                this.setState({
                    loading:                true,
                    resultKey,
                }, () => {
                    getVariants({
                        dataId:             this.context.openedPlan.dataSampleId,
                        documentPlanId:     this.context.openedPlan.id,
                        readerFlagValues:   {},
                    }).then( result => this.setState( state =>
                        ( state.resultKey === resultKey ) && {
                            error:          false,
                            loading:        false,
                            result,
                        }
                    )).catch( error => this.setState( state =>
                        ( state.resultKey === resultKey ) && {
                            error,
                            loading:        false,
                        }
                    ));
                });
            }
        }
        /// TODO: implement pre-loading status
    }

    componentDidMount() {
        this.onUpdates();
    }

    componentDidUpdate() {
        this.onUpdates();
    }

    render = ({ children }, state ) =>
        <Context.Provider
            children={ children }
            value={ state }
        />;
}
