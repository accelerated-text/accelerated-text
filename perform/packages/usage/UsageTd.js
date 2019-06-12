import classnames           from 'classnames';
import { h, Component }     from 'preact';

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
            <td className={ classnames( S.className, S[usage], className ) }>
                <span
                    children="✔️"
                    className={ S.YES }
                    onClick={ this.onClickYes }
                />
                {
                    defaultUsage
                        ? null
                        : <span
                            children="⚪️"
                            className={ S.DONT_CARE }
                            onClick={ this.onClickDontCare }
                        />
                }
                <span
                    children="❌"
                    className={ S.NO }
                    onClick={ this.onClickNo }
                />
            </td>
        );
    }
}
