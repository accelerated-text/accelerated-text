import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { searchPhrases }        from '../graphql/queries.graphql';

import Phrases                  from './Phrases';
import S                        from './Search.sass';


export default composeQueries({
    searchPhrases:              [ searchPhrases, { query: 'query' }],
})(({
    className,
    onChangeQuery,
    onClickPhrase,
    query,
    searchPhrases: { error, loading, searchPhrases },
}) =>
    <div className={ classnames( S.className, className ) }>
        <input
            className={ S.input }
            onChange={ onChangeQuery }
            type="search"
            value={ query }
        />
        { error
            ? <Error message={ error } />
        : loading
            ? <Loading />
        : searchPhrases
            ?  <Phrases
                onClickPhrase={ onClickPhrase }
                phrases={ searchPhrases }
            />
            : <Info message="no results found" />
        }
    </div>
);
