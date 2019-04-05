export const sortByCreatedAt = ( a, b ) => (
    ( a.createdAt && b.createdAt )
        ? ( b.createdAt - a.createdAt )
    : a.createdAt
        ? -1
    : b.createdAt
        ? 1
    : -1
);

export const sortPlans = list =>
    list.sort( sortByCreatedAt );

export const getOpenedPlan = getStoreState =>
    getStoreState( 'documentPlans' ).plans[
        getStoreState( 'planList' ).openedPlanUid
    ];
