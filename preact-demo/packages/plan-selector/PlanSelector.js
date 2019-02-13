import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import planTemplate         from '../document-plans/plan-template';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import ItemControls         from './ItemControls';
import List                 from './List';
import S                    from './PlanSelector.sass';
import Status               from './Status';


export default useStores([
    'documentPlans',
    'planList',
])( class PlanSelector extends Component {

    onClickNew = evt => {
        const name =        window.prompt( 'Add a new Document Plan:', planTemplate.name );
        name && this.props.E.planList.onAddNew({ name });
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

        return (
            <div className={ S.className }>{
                isLoading
                    ? <Loading message="Loading plans." />
                : isLoadError
                    ? <Error message="Loading error! Please refresh the page." />
                : noPlans
                    ? <button
                        onClick={ this.onClickNew }
                        children="➕ New document plan"
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
                            onChangeSelected={ E.planList.onSelectPlan }
                            plans={ plans }
                            selectedUid={ openedPlanUid }
                            uids={ uids }
                        />,
                        <ItemControls
                            onDelete={ E.documentPlans.onDelete }
                            onUpdate={ E.documentPlans.onUpdate }
                            plan={ plans[openedPlanUid] }
                            status={ statuses[openedPlanUid] }
                        />,
                    ]
            }</div>
        );
    }
});
