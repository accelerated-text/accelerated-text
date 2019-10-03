import { createContext }    from 'preact';


export default createContext({
    ChildComponent:         null,
    childElement:           null,
    childProps:             null,
    closeBar:               null,
    onCloseBar:             null,
    onCloseFn:              null,
    openComponentBar:       null,
    openElementBar:         null,
});
