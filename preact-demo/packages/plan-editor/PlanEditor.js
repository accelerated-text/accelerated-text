import classnames       from 'classnames';
import { h, Component } from 'preact';

import Error            from '../ui-messages/Error';
import Loading          from '../ui-messages/Loading';
import OnboardCode      from '../onboard-code/OnboardCode';
import planTemplate     from '../document-plans/plan-template';
import { useStores }    from '../vesa/';
import Workspace        from '../nlg-workspace/NlgWorkspace';

import Header           from './Header';
import OnboardData      from './onboard/Data';
import { QA }           from './qa.constants';
import S                from './PlanEditor.sass';


export default useStores([
    'documentPlans',
    'planList',
])( class PlanEditor extends Component {

    onChangeWorkspace = ({ documentPlan, workspaceXml }) => {
        const {
            E,
            documentPlans:  { plans },
            planList:       { openedPlanUid },
        } = this.props;

        E.documentPlans.onUpdate({
            ...plans[openedPlanUid],
            documentPlan,
            blocklyXml:     workspaceXml,
        });
    }

    onCreateXml = blocklyXml =>
        this.props.E.planList.onCreate({
            name:           planTemplate.name,
            blocklyXml,
        })

    render({
        E,
        documentPlans: {
            plans,
        },
        planList: {
            getListError,
            getListLoading,
            openedPlanUid,
            uids,
        },
    }) {
        const openedPlan =  openedPlanUid && plans[openedPlanUid];

        return (
            <div className={ S.className }>
                <Header className={ QA.HEADER } />
                <div className={ classnames( S.body, QA.BODY ) }>
                    { getListError &&
                        <Error className={ S.item } message="Error loading document plans." />
                    }
                    { getListLoading
                        ? <Loading className={ S.item } message="Loading document plans." />
                        : (
                            <OnboardData>
                                <OnboardCode
                                    hasCode={ !!openedPlan }
                                    onCreateXml={ this.onCreateXml }
                                />
                            </OnboardData>
                        )
                    }
                    { openedPlan &&
                        <Workspace
                            key={ openedPlanUid }
                            onChangeWorkspace={ this.onChangeWorkspace }
                            workspaceXml={ openedPlan.blocklyXml }
                        />
                    }
                </div>
            </div>
        );
    }
});
