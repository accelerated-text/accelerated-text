import classnames           from 'classnames';
import { h }                from 'preact';

import S                    from './UsageTd.sass';


export default ({
    className,
    usage,
}) =>
    <td className={ classnames( S.className, S[usage], className ) }>
        <span children="✔️" className={ S.YES } />
        <span children="⚪️" className={ S.DONT_CARE } />
        <span children="❌" className={ S.NO } />
    </td>;
