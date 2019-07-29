import {
    h,
    Component,
    createRef,
}                           from 'preact';

import S                    from './Modal.sass';


export default class QuickSearchModal extends Component {

    inputRef =              createRef();

    onClickBackground = evt =>
        this.props.onClose();

    onClickModal = evt => {
        evt.stopPropagation();
    };

    onKeyDown = evt => {
        if( evt.key === 'Escape' ) {
            this.props.onClose();
        }
    };

    componentDidMount() {

        this.inputRef.current.focus();
    }
    
    render() {
        return (
            <div
                className={ S.className }
                onClick={ this.onClickBackground }
                onKeyDown={ this.onKeyDown }
                tabindex="0"
            >
                <div className={ S.modal } onClick={ this.onClickModal }>
                    <h1>Search</h1>
                    <form>
                        <input ref={ this.inputRef } type="search" />
                    </form>
                    <div className={ S.results }>
                        ...results
                    </div>
                </div>
            </div>
        );
    }
}
