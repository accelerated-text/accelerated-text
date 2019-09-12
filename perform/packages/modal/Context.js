import { createContext }    from 'preact';


export default createContext({
    ChildComponent:         null,
    childElement:           null,
    childProps:             null,
    closeModal:             null,
    onCloseModal:           null,
    onCloseFn:              null,
    openModal:              null,
});
