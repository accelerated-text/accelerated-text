import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import Error                from '../ui-messages/Error';
import { findFileByPlan }   from '../data-samples/functions';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import Loading              from '../ui-messages/Loading';
import OnboardCode          from '../onboard-code/OnboardCode';
import planTemplate         from '../document-plans/plan-template';
import { useStores }        from '../vesa/';
import Workspace            from '../nlg-workspace/NlgWorkspace';

import S                    from './PlanEditor.sass';


export default useStores([
    'dataSamples',
    'documentPlans',
    'planList',
])( class PlanEditor extends Component {

    static propTypes = {
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

        return (
            <div className={ classnames( S.className, className ) }>{
                openedPlan
                    ? <Workspace
                        cellNames={ planFile && planFile.fieldNames }
                        key={ openedPlanUid }
                        onChangeWorkspace={ this.onChangeWorkspace }
                        workspaceXml={ openedPlan.blocklyXml }
                    />
                : getListLoading
                    ? <Loading className={ S.item } message="Loading data." />
                : getListError
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
});
