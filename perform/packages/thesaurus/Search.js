import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { searchWords }          from '../graphql/queries.graphql';

import Words                    from './Words';
import S                        from './Search.sass';


export default composeQueries({
    searchWords:              [ searchWords, { query: 'query' }],
})(({
    className,
    onChangeQuery,
    onClickWord,
    query,
    searchWords: { error, loading, searchWords },
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
        : searchWords
            ?  <Words
                onClickWord={ onClickWord }
                words={ searchWords }
            />
            : <Info message="no results found" />
        }
    </div>
);
