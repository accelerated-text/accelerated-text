import {
    h,
    Component,
    createRef,
}                           from 'preact';
import PropTypes            from 'prop-types';


export default class QuickJump extends Component {

    static propTypes = {
        workspace:          PropTypes.object.isRequired,
        Blockly:            PropTypes.object.isRequired,
    };

    inputRef =              createRef();

    state = {
        query:              '',
    };

    onInput = evt => {
        const query =       evt.target.value;
        this.setState({ query });
    }

    onKeyDown = evt => {

    };

    componentDidMount() {
        this.inputRef.current.focus();
    }

    render( _, { query }) {
        return (
            <input
                onInput={ this.onInput }
                ref={ this.inputRef }
                type="text"
                value={ query }
            />
        );
    }
}
