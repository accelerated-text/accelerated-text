import { h } from 'preact';

import S from './Word.sass';

export default ({ block: { content }}) =>
    ( !content )
        ? null
        : (
            <div className={ `${ S.className } qa-blocks-word` }>
                { content }
            </div>
        );
