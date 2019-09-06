import { createContext }    from 'preact';


export default createContext({
    documentPlans:          null,
    documentPlansError:     null,
    documentPlansLoading:   true,
    E:                      null,
    openPlan:               null,
    openPlanUid:            null,
    openedPlan:             null,
    openedPlanLoading:      true,
});
