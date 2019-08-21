import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { synonyms }             from '../graphql/queries.graphql';

import Words                    from './Words';
import S                        from './Synonyms.sass';


export default composeQueries({
    synonyms:                   [ synonyms, {
        wordId:                 [ 'word', 'id' ],
    }],
})(({
    className,
    onClickBack,
    onClickWord,
    word,
    synonyms: { error, loading, synonyms },
}) =>
    <div className={ classnames( S.className, className ) }>
        <button className={ S.back } onClick={ onClickBack }>
            « to search 🔎
        </button>
        <h4 className={ S.word }>
            { word.text }
        </h4>
        { error
            ? <Error message={ error } />
        : loading
            ? <Loading />
        : ( synonyms && synonyms.synonyms && synonyms.synonyms.length )
            ? <Words
                onClickWord={ onClickWord }
                words={ synonyms.synonyms }
            />
            : <Info message="no synonyms found" />
        }
    </div>
);
