import { createContext }    from 'preact';


export default createContext({
    error:                  null,
    loading:                true,
    openPlan:               null,
    openPlanUid:            null,
    plan:                   null,
    previewData:            null,
});
