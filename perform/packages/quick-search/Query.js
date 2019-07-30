import {
    h,
    Component,
    createRef,
}                           from 'preact';


export default class QuickSearchQuery extends Component {

    inputRef =              createRef();

    onInput = evt => {
        this.props.onChange( evt.target.value );
    };

    onSubmit = evt => {
        evt.preventDefault();
    };

    componentDidMount() {
        if( this.props.autofocus ) {
            this.inputRef.current.focus();
        }
    }

    render({ value }) {
        return (
            <form onSubmit={ this.onSubmit }>
                <input
                    onInput={ this.onInput }
                    ref={ this.inputRef }
                    type="search"
                    value={ value }
                />
            </form>
        );
    }
}
