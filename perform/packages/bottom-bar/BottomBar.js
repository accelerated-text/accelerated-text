import { h, Component }     from 'preact';

import Context              from './Context';
import S                    from './BottomBar.sass';


export default class BottomBar extends Component {

    static contextType =    Context;

    onClose = () => {
        this.context.closeBar();
        this.props.onClose && this.props.onClose();
    };

    onClickBar = evt => {
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
                    <div className={ S.bar } onClick={ this.onClickBar }>
                        <button
                            children="✖️ close"
                            className={ S.close }
                            onClick={ this.onClose }
                        />
                        { children
                            || childElement
                            || <ChildComponent { ...childProps } />
                        }
                    </div>
                </div>
            );
        }
    }
}
