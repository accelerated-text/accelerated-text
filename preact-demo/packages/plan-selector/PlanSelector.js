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
import Status               from './Status';


export default useStores([
    'documentPlans',
    'planList',
])( class PlanSelector extends Component {

    onClickNew = evt => {
        const {
            E,
            openedPlan,
        } = this.props;

        const name = window.prompt(         // eslint-disable-line no-alert
            'Add a new Document Plan:',
            planTemplate.name,
        );
        name && E.planList.onAddNew({
            contextId:      openedPlan.contextId || null,
            dataSampleId:   openedPlan.dataSampleId || null,
            name,
        });
    }

    onClickSaveAs = evt => {
        const {
            E,
            openedPlan,
        } = this.props;

        const name = window.prompt(         // eslint-disable-line no-alert
            'Enter name for the new plan:',
            openedPlan.name,
        );
        name && E.planList.onAddNew({
            ...openedPlan,
            name,
        });
    }

    render({
        E,
        documentPlans: {
            plans,
            statuses,
        },
        openedPlan,
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
                : !openedPlan
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
                    ]
            }</div>
        );
    }
});
