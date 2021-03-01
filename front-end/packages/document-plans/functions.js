export const preparePlanJson = plan => ({
    ...plan,
    documentPlan: (
        typeof plan.documentPlan === 'string'
            ? plan.documentPlan
            : JSON.stringify( plan.documentPlan )
    ),
});


export const getPlanByUid = ( documentPlans, uid ) => (
    uid
    && documentPlans
    && documentPlans.items
    && documentPlans.items.find(
        item => item.uid === uid
    )
);


export const sortByCreatedAt = ( a, b ) => (
    ( a.createdAt && b.createdAt )
        ? ( b.createdAt - a.createdAt )
    : a.createdAt
        ? -1
    : b.createdAt
        ? 1
    : -1
);
