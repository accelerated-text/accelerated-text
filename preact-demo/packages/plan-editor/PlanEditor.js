import classnames       from 'classnames';
import { h, Component } from 'preact';

import Error            from '../ui-messages/Error';
import getOpenedPlan    from '../plan-list/get-opened-plan';
import Loading          from '../ui-messages/Loading';
import OnboardCode      from '../onboard-code/OnboardCode';
import planTemplate     from '../document-plans/plan-template';
import { useStores }    from '../vesa/';
import Workspace        from '../nlg-workspace/NlgWorkspace';

import S                from './PlanEditor.sass';


export default useStores([
    'documentPlans',
    'planList',
])( class PlanEditor extends Component {

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
        planList: {
            getListError,
            getListLoading,
            openedPlanUid,
            uids,
        },
    }) {
        const openedPlan =  getOpenedPlan( this.props );

        return (
            <div className={ classnames( S.className, className ) }>{
                openedPlan
                    ? <Workspace
                        key={ openedPlanUid }
                        onChangeWorkspace={ this.onChangeWorkspace }
                        workspaceXml={ openedPlan.blocklyXml }
                    />
                : getListLoading
                    ? <Loading className={ S.item } message="Loading document plans." />
                : getListError
                    ?  <Error className={ S.item } message="Error loading document plans." />
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
