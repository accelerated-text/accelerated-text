import classnames               from 'classnames';
import { h }                    from 'preact';

import S                        from './Phrases.sass';


export default ({
    className,
    onClickPhrase,
    phrases,
}) =>
    <ol className={ classnames( S.className, className ) }>{
        phrases && phrases.map(
            phrase =>
                <li
                    key={ phrase.id }
                    children={ phrase.text }
                    onClick={ () => onClickPhrase( phrase ) }
                />
        )
    }</ol>;
