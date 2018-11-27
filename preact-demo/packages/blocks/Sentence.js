import { h }            from 'preact';

import ABlock           from './ABlock';
import S                from './Sentence.sass';

export default ({ block: { children }}) =>
    ( !children )
        ? null
        : (
            <div className={ `${ S.className } qa-blocks-sentence` }>
                { children.map(
                    block => <ABlock block={ block } />
                )}
            </div>
        );
