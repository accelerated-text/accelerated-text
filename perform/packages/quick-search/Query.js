import {
    h,
    Component,
    createRef,
}                           from 'preact';

import S                    from './Query.sass';


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
            <form className={ S.className } onSubmit={ this.onSubmit }>
                <input
                    onInput={ this.onInput }
                    placeholder="Search blocks"
                    ref={ this.inputRef }
                    type="search"
                    value={ value }
                />
            </form>
        );
    }
}
