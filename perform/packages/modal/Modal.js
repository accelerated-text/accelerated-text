import { h, Component }     from 'preact';

import Context              from './Context';
import S                    from './Modal.sass';


export default class Modal extends Component {

    static contextType =    Context;

    onClose = () => {
        this.context.closeModal();
        this.props.onClose && this.props.onClose();
    };

    onClickModal = evt => {
        evt.stopPropagation();
    };

    onKeyDown = evt => {
        if( evt.key === 'Escape' ) {
            this.onClose();
        }
    };

    render(
        { className, children },
        _,
        { ChildComponent, childElement, childProps },
    ) {
        if( children || ChildComponent || childElement ) {
            return (
                <div
                    className={ S.className }
                    onClick={ this.onClose }
                    onKeyDown={ this.onKeyDown }
                    tabIndex="0"
                >
                    <div className={ S.modal } onClick={ this.onClickModal }>{
                        children
                            ? children
                        : childElement
                            ? childElement
                        : ChildComponent
                            ? <ChildComponent { ...childProps } />
                            : null
                    }</div>
                </div>
            );
        }
    }
}
