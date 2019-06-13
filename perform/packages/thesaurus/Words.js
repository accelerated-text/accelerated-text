import classnames               from 'classnames';
import { h }                    from 'preact';

import Word                     from './Word';
import S                        from './Words.sass';


export default ({
    className,
    onClickWord,
    words,
}) =>
    <ol className={ classnames( S.className, className ) }>
        { words && words.map( word =>
            <Word key={ word.id } onClick={ onClickWord } word={ word } />
        )}
    </ol>;
