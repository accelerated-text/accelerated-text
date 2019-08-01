import { h, Component }     from 'preact';

import S                    from './Modal.sass';


export default class Modal extends Component {

    onClose = () => {
        this.props.onClose();
    };

    onClickModal = evt => {
        evt.stopPropagation();
    };

    onKeyDown = evt => {
        if( evt.key === 'Escape' ) {
            this.onClose();
        }
    };

    render = ({ className, children }) =>
        <div
            className={ S.className }
            onClick={ this.onClose }
            onKeyDown={ this.onKeyDown }
            tabIndex="0"
        >
            <div
                children={ children }
                className={ S.modal }
                onClick={ this.onClickModal }
            />
        </div>;
}
