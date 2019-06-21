import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { QA }               from '../tests/constants';

import S                    from './UsageTd.sass';


export default class UsageTd extends Component {

    onClickDontCare =       () => this.props.onChange( 'DONT_CARE' );
    onClickNo =             () => this.props.onChange( 'NO' );
    onClickYes =            () => this.props.onChange( 'YES' );

    render({
        className,
        defaultUsage,
        onChange,
        usage,
    }) {
        return (
            <td className={ classnames( S.className, S[usage], className, QA.USAGE_TD, QA[usage]) }>
                <span
                    children="✔️"
                    className={ classnames( S.YES, QA.USAGE_TD_YES ) }
                    onClick={ this.onClickYes }
                />
                {
                    defaultUsage
                        ? null
                        : <span
                            children="⚪️"
                            className={ classnames( S.DONT_CARE, QA.USAGE_TD_DONT_CARE ) }
                            onClick={ this.onClickDontCare }
                        />
                }
                <span
                    children="❌"
                    className={ classnames( S.NO, QA.USAGE_TD_NO ) }
                    onClick={ this.onClickNo }
                />
            </td>
        );
    }
}
