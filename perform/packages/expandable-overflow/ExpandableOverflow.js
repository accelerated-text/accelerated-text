import  classnames          from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import S                    from './ExpandableOverflow.sass';


export default class ExpandableOverflow extends Component {

    static propTypes = {
        className:          PropTypes.string,
        children:           PropTypes.node,
        collapsedClassName: PropTypes.string,
        expandedClassName:  PropTypes.string,
        isExpanded:         PropTypes.bool,
    };

    state = {
        isCollapsed:        ! this.props.isExpanded,
    };

    onClickAll = () => {
        if( this.state.isCollapsed ) {
            this.setState({
                isCollapsed:    ! this.state.isCollapsed,
            });
        }
    };

    onClickIcon = evt => {
        evt.stopPropagation();

        this.setState({
            isCollapsed:        ! this.state.isCollapsed,
        });
    };

    render({
        children,
        className,
        collapsedClassName,
        expandedClassName,
    }, {
        isCollapsed,
    }) {
        return (
            <div
                className={ classnames(
                    S.className,
                    isCollapsed && S.isCollapsed,
                    isCollapsed && collapsedClassName,
                    ! isCollapsed && expandedClassName,
                    className,
                ) }
                onClick={ this.onClickAll }
            >
                <div
                    children="▶️"
                    className={ S.expandIcon }
                    onclick={ this.onClickIcon }
                />
                { children }
            </div>
        );
    }
}
