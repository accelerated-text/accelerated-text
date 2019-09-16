import { h, Component }     from 'preact';

import Context              from './Context';


export default class ReaderContextProvider extends Component {

    state = {
        flagValues:         {},

        onToggleFlag: flagId => {
            this.setState( state => ({
                flagValues: {
                    ...state.flagValues,
                    [flagId]:   ! state.flagValues[flagId],
                },
            }));
        },
    };

    render = ({ children }, state ) =>
        <Context.Provider
            children={ children }
            value={ this.state }
        />;
}
