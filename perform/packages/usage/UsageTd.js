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
        error,
        loading,
        onChange,
        usage,
    }) {
        return (
            <td
                className={ classnames(
                    S.className,
                    S[usage],
                    error && S.error,
                    loading && S.loading,
                    className,
                    QA.USAGE_TD,
                    QA[usage],
                ) }
                title={ error || ( loading && 'Saving...' ) }
            >
                <span
                    children="✔️"
                    className={ classnames( S.YES, QA.USAGE_TD_YES ) }
                    onClick={ !loading ? this.onClickYes : null }
                />
                {
                    defaultUsage
                        ? null
                        : <span
                            children="⚪️"
                            className={ classnames( S.DONT_CARE, QA.USAGE_TD_DONT_CARE ) }
                            onClick={ !loading ? this.onClickDontCare : null }
                        />
                }
                <span
                    children="❌"
                    className={ classnames( S.NO, QA.USAGE_TD_NO ) }
                    onClick={ !loading ? this.onClickNo : null }
                />
            </td>
        );
    }
}
