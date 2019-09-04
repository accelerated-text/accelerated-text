import { createContext }    from 'preact';


export default createContext({
    openedDataFile:         null,
    openedDataFileError:    null,
    openedDataFileLoading:  false,
    openedPlan:             null,
    openedPlanStatus:       {},
});
