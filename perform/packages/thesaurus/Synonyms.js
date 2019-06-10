import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { synonyms }             from '../graphql/queries.graphql';

import Phrases                  from './Phrases';
import S                        from './Synonyms.sass';


export default composeQueries({
    synonyms:                   [ synonyms, {
        phraseId:               [ 'phrase', 'id' ],
    }],
})(({
    className,
    onClickBack,
    onClickPhrase,
    phrase,
    synonyms: { error, loading, synonyms },
}) =>
    <div className={ classnames( S.className, className ) }>
        <button className={ S.back } onClick={ onClickBack }>
            🔙🔎
        </button>
        <h4 className={ S.phrase }>
            { phrase.text }
        </h4>
        { error
            ? <Error message={ error } />
        : loading
            ? <Loading />
        : ( synonyms && synonyms.phrases && synonyms.phrases.length )
            ? <Phrases
                onClickPhrase={ onClickPhrase }
                phrases={ synonyms.phrases }
            />
            : <Info message="no synonyms found" />
        }
    </div>
);
