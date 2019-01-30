import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import * as planList        from '../plan-list/functions';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import ItemControls         from './ItemControls';
import List                 from './List';
import S                    from './PlanSelector.sass';


const DEFAULT_NAME =        'Untitled Plan';


export default useStores([
    'planList',
])( class PlanSelector extends Component {

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', DEFAULT_NAME );
        planName && this.props.E.planList.onAddNew( planName );
    }

    render({
        planList: {
            getListError,
            getListLoading,
            openedPlanUid,
            plans,
            statuses,
        },
    }) {
        const hasPlans =    plans && plans.length;
        const noPlans =     !hasPlans;

        const isLoading = (
            noPlans && getListLoading
        );

        const isLoadError = (
            noPlans && getListError
        );

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
                        <List onClickNew={ this.onClickNew } />,
                        <ItemControls
                            item={ planList.findByUid( plans, openedPlanUid ) }
                            status={ statuses[openedPlanUid] }
                        />,
                    ]
            }</div>
        );
    }
});
