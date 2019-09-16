import { h, Component }     from 'preact';

import Context              from './Context';


export default class ModalContextProvider extends Component {

    state = {
        childElement:           null,
        ChildComponent:         null,
        childProps:             null,
        onCloseFn:              null,

        closeModal: () => {
            this.state.onCloseFn && this.state.onCloseFn();
            this.setState({
                ChildComponent: null,
                childElement:   null,
                childProps:     null,
                onCloseFn:      null,
            });
        },

        onCloseModal: onCloseFn =>
            this.setState({
                onCloseFn,
            }),

        openComponentModal: ( ChildComponent, childProps ) =>
            this.setState({
                ChildComponent,
                childElement:   null,
                childProps,
                onCloseFn:      null,
            }),

        openElementModal: childElement =>
            this.setState({
                ChildComponent: null,
                childElement,
                childProps:     null,
                onCloseFn:      null,
            }),
    };

    render = ({ children }, state ) =>
        <Context.Provider
            children={ children }
            value={ state }
        />;
}
