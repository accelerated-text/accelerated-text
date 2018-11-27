import { h } from 'preact';

import S from './Token.sass';

export default ({ block: { text }}) =>
    ( !text )
        ? null
        : (
            <div className={ `${ S.className } qa-blocks-word` }>
                { text }
            </div>
        );
