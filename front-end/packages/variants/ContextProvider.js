import { h, Component }     from 'preact';

import composeContexts      from '../compose-contexts/';
import { getPlanDataRecord }    from '../data-samples/functions';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import ReaderContext        from '../reader/Context';

import Context              from './Context';
import { getVariants, checkStatus }      from './api';


const canGetResult = async ( plan, file ) => {
    const serverStatus = await checkStatus();

    return {serverStatus, planAvailable: (plan && plan.id && plan.updatedAt)};
};


const getResultKey = ( plan, file, flagValues ) =>
    JSON.stringify({
        flagValues,
        planId:             plan.id,
        planUpdateCount:    plan.updateCount,
        /// TODO: https://gitlab.com/tokenmill/nlg/accelerated-text/issues/274
    });


export default composeContexts({
    openedDataFile:         OpenedFileContext,
    openedPlan:             OpenedPlanContext,
    reader:                 ReaderContext,
})( class VariantsContextProvider extends Component {

    state = {
        error:              null,
        loading:            false,
        result:             null,
        resultKey:          null,
    };

    onUpdates = () => {
        const {
            openedDataFile: { file },
            openedPlan: { plan },
            reader,
        } = this.props;

        canGetResult( plan, file ).then( s => {
            const { serverStatus, planAvailable } = s;
            if ( !serverStatus ) {
                this.setState({loading: false, error: "API is in an unhealthy state. Please contact Administrator."})
            }
            else if ( planAvailable ) {
                const resultKey = getResultKey( plan, file, reader.flagValues );
                if( this.state.resultKey !== resultKey ) {
                    this.setState({
                        loading:                true,
                        resultKey,
                    }, () => {
                        getVariants({
                            dataId:             plan.dataSampleId != null ? plan.dataSampleId : undefined,
                            documentPlanId:     plan.id,
                            readerFlagValues:   reader.flagValues,
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
        })
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
});
