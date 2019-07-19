import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { concepts }         from '../graphql/queries.graphql';
import Error                from '../ui-messages/Error';
import { findFileByPlan }   from '../data-samples/functions';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import Loading              from '../ui-messages/Loading';
import OnboardCode          from '../onboard-code/OnboardCode';
import planTemplate         from '../document-plans/plan-template';
import { useStores }        from '../vesa/';
import Workspace            from '../nlg-workspace/NlgWorkspace';

import S                    from './PlanEditor.sass';


export default composeQueries({
    concepts,
})( useStores([
    'dataSamples',
    'documentPlans',
    'planList',
])( class PlanEditor extends Component {

    static propTypes = {
        concepts:           PropTypes.object.isRequired,
        dataSamples:        PropTypes.object.isRequired,
        documentPlans:      PropTypes.object.isRequired,
        planList:           PropTypes.object.isRequired,
        className:          PropTypes.string,
    };

    onChangeWorkspace = ({ documentPlan, workspaceXml }) =>
        this.props.E.documentPlans.onUpdate({
            ...getOpenedPlan( this.props ),
            documentPlan,
            blocklyXml:     workspaceXml,
        });

    onCreateXml = blocklyXml =>
        this.props.E.planList.onCreate({
            name:           planTemplate.name,
            blocklyXml,
        })

    render({
        className,
        concepts: {
            concepts:       conceptsResult,
            error:          conceptsError,
            loading:        conceptsLoading,
        },
        dataSamples,
        planList: {
            getListError,
            getListLoading,
            openedPlanUid,
            uids,
        },
    }) {
        const openedPlan =  getOpenedPlan( this.props );
        const planFile =    findFileByPlan( dataSamples, openedPlan );

        const hasData =     openedPlan && conceptsResult;
        const hasError =    getListError || conceptsError;
        const isLoading =   getListLoading || conceptsLoading;

        return (
            <div className={ classnames( S.className, className ) }>{
                hasData
                    ? <Workspace
                        amrConcepts={ conceptsResult.concepts }
                        cellNames={ planFile && planFile.fieldNames }
                        key={ openedPlanUid }
                        onChangeWorkspace={ this.onChangeWorkspace }
                        workspaceXml={ openedPlan.blocklyXml }
                    />
                : isLoading
                    ? <Loading className={ S.item } message="Loading data." />
                : hasError
                    ?  <Error className={ S.item } message="Error loading data." />
                    : (
                        <OnboardCode
                            hasCode={ !!openedPlan }
                            onCreateXml={ this.onCreateXml }
                        />
                    )
            }</div>
        );
    }
}));
