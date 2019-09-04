import { createContext }    from 'preact';


export default createContext({
    E:                      null,
    openedDataFile:         null,
    openedDataFileError:    null,
    openedDataFileLoading:  false,
    openedPlan:             null,
    openedPlanStatus:       {},
});
