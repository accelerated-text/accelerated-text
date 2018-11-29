import classnames       from 'classnames';
import { h }            from 'preact';

import S from './Token.sass';

export default ({ block: { text, type }}) =>
    ( !text )
        ? null
        : (
            <div className={ classnames(
                S.className,
                S[type],
                'qa-blocks-token',
            ) }>
                { text }
            </div>
        );
