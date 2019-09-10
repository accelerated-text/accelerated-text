import { h, Component }     from 'preact';

import composeContexts      from '../compose-contexts/';
import { getPlanDataRecord }    from '../data-samples/functions';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import ReaderContext        from '../reader/Context';

import Context              from './Context';
import { getVariants }      from './api';


const canGetResult = ( plan, file ) => (
    getPlanDataRecord( file, plan )
    && plan.id
    && plan.updatedAt
);


const getResultKey = ( plan, file, flagValues ) =>
    JSON.stringify({
        flagValues,
        planId:             plan.id,
        planUpdateCount:    plan.updateCount,
        /// TODO: use the data file
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

        if( canGetResult( plan, file )) {
            const resultKey = getResultKey( plan, file, reader.flagValues );
            if( this.state.resultKey !== resultKey ) {
                this.setState({
                    loading:                true,
                    resultKey,
                }, () => {
                    getVariants({
                        dataId:             plan.dataSampleId,
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
});
