const OPENED_PLAN_UID =     'plan-list/local-storage-adapter#openedPlanUid';

const saveOpenedPlanUid = ( _, { getStoreState }) =>
    localStorage.setItem(
        OPENED_PLAN_UID,
        getStoreState( 'planList' ).openedPlanUid,
    );


export default {

    componentDidMount: ( _, { E }) => {

        const openedPlanUid =   localStorage.getItem( OPENED_PLAN_UID );
        if( openedPlanUid ) {
            E.planList.onSelectPlan( openedPlanUid );
        }
    },

    documentPlans: {

        onCreate:           saveOpenedPlanUid,
    },

    planList: {

        onSelectPlan:       saveOpenedPlanUid,
        onGetListResult:    saveOpenedPlanUid,
    },
};

