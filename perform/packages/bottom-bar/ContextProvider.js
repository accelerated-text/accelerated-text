import { h, Component }     from 'preact';

import Context              from './Context';


export default class BottomBarProvider extends Component {
    
    state = {
        childElement:           null,
        ChildComponent:         null,
        childProps:             null,
        onCloseFn:              null,

        closeBar: () => {
            this.state.onCloseFn && this.state.onCloseFn();
            this.setState({
                ChildComponent: null,
                childElement:   null,
                childProps:     null,
                onCloseFn:      null,
            });
        },

        onCloseBar: onCloseFn =>
            this.setState({
                onCloseFn,
            }),

        openComponentBar: ( ChildComponent, childProps ) =>
            this.setState({
                ChildComponent,
                childElement:   null,
                childProps,
                onCloseFn:      null,
            }),

        openElementBar: childElement =>
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
