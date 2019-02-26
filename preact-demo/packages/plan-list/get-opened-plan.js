export default ({
    documentPlans:  { plans },
    planList:       { openedPlanUid },
}) => (
    plans && openedPlanUid && plans[openedPlanUid]
    || null
);
