import  classnames          from 'classnames';
import { h, Component }     from 'preact';

import S                    from './Item.sass';


export default class SidebarItem extends Component {

    state = {
        isExpanded:         this.props.isExpanded,
    };

    onClickHeader = () =>
        this.setState({
            isExpanded:     !this.state.isExpanded,
        });

    render({ children, className, title }, { isExpanded }) {
        return (
            <div className={ classnames(
                S.className,
                isExpanded && S.isExpanded,
                className,
            ) }>
                <div className={ S.header } onClick={ this.onClickHeader }>
                    <div className={ S.title }>{ title }</div>
                    <div className={ S.expandIcon }>▶️</div>
                </div>
                <div className={ S.body }>
                    { children }
                </div>
            </div>
        );
    }
}
