import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import planTemplate         from '../document-plans/plan-template';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import ItemControls         from './ItemControls';
import List                 from './List';
import { QA }               from './qa.constants';
import S                    from './PlanSelector.sass';
import SelectContext        from './SelectContext';
import Status               from './Status';


export default useStores([
    'documentPlans',
    'planList',
])( class PlanSelector extends Component {

    onClickNew = evt => {
        const name = window.prompt(         // eslint-disable-line no-alert
            'Add a new Document Plan:',
            planTemplate.name,
        );
        name && this.props.E.planList.onAddNew({ name });
    }

    onClickSaveAs = evt => {
        const {
            E,
            documentPlans:  { plans },
            planList:       { openedPlanUid },
        } = this.props;

        const currentPlan = plans[openedPlanUid];
        const name = window.prompt(         // eslint-disable-line no-alert
            'Enter name for the new plan:',
            currentPlan.name,
        );
        name && E.planList.onAddNew({ ...currentPlan, name });
    }

    render({
        E,
        documentPlans: {
            plans,
            statuses,
        },
        planList: {
            getListError,
            getListLoading,
            openedPlanUid,
            uids,
        },
    }) {
        const hasPlans =    uids && uids.length;
        const noPlans =     !hasPlans;
        const isLoading =   noPlans && getListLoading;
        const isLoadError = noPlans && getListError;
        const openedPlan  = plans && openedPlanUid && plans[openedPlanUid];

        return (
            <div className={ S.className }>{
                isLoading
                    ? <Loading message="Loading plans." />
                : isLoadError
                    ? <Error message="Loading error! Please refresh the page." />
                : noPlans
                    ? <button
                        children="➕ New document plan"
                        className={ QA.BTN_NEW_PLAN }
                        onClick={ this.onClickNew }
                    />
                : !openedPlanUid
                    ? <UnexpectedWarning />
                    : [
                        <Status
                            className={ S.status }
                            listStatus={ this.props.planList }
                            planStatuses={ statuses }
                            uids={ uids }
                        />,
                        <List
                            onClickNew={ this.onClickNew }
                            onClickSaveAs={ this.onClickSaveAs }
                            onChangeSelected={ E.planList.onSelectPlan }
                            plans={ plans }
                            selectedUid={ openedPlanUid }
                            uids={ uids }
                        />,
                        <ItemControls
                            onDelete={ E.documentPlans.onDelete }
                            onUpdate={ E.documentPlans.onUpdate }
                            plan={ openedPlan }
                            status={ statuses[openedPlanUid] }
                        />,
                        <span className={ S.contextLabel }>Context: </span>,
                        <SelectContext plan={ openedPlan } />,
                    ]
            }</div>
        );
    }
});
