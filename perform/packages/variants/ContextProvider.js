import { h, Component }     from 'preact';

import composeContexts      from '../compose-contexts/';
import DocumentPlansContext from '../document-plans/Context';
import { getPlanDataRecord }    from '../data-samples/functions';
import ReaderContext        from '../reader/Context';

import Context              from './Context';
import { getVariants }      from './api';


const canGetResult = ({ openedPlan, openedDataFile }) => (
    getPlanDataRecord( openedDataFile, openedPlan )
    && openedPlan.id
    && openedPlan.updatedAt
);


const getResultKey = ( openedPlan, openedDataFile, flagValues ) =>
    JSON.stringify({
        flagValues,
        planId:             openedPlan.id,
        planUpdatedAt:      openedPlan.updatedAt,
    });


export default composeContexts({
    documentPlans:          DocumentPlansContext,
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
            documentPlans,
            reader,
        } = this.props;

        if( canGetResult( documentPlans )) {
            const resultKey = getResultKey(
                documentPlans.openedPlan,
                documentPlans.openedDataFile,
                reader.flagValues,
            );
            if( this.state.resultKey !== resultKey ) {
                this.setState({
                    loading:                true,
                    resultKey,
                }, () => {
                    getVariants({
                        dataId:             documentPlans.openedPlan.dataSampleId,
                        documentPlanId:     documentPlans.openedPlan.id,
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
