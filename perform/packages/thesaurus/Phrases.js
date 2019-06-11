import classnames               from 'classnames';
import { h }                    from 'preact';

import Phrase                   from './Phrase';
import S                        from './Phrases.sass';


export default ({
    className,
    onClickPhrase,
    phrases,
}) =>
    <ol className={ classnames( S.className, className ) }>
        { phrases && phrases.map( phrase =>
            <Phrase key={ phrase.id } onClick={ onClickPhrase } phrase={ phrase } />
        )}
    </ol>;
