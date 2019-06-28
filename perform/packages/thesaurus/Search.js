import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { searchThesaurus }      from '../graphql/queries.graphql';

import Words                    from './Words';
import S                        from './Search.sass';


export default composeQueries({
    searchThesaurus:            [ searchThesaurus, { query: 'query' }],
})(({
    className,
    onChangeQuery,
    onClickWord,
    query,
    searchThesaurus: { error, loading, searchThesaurus },
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
        : searchThesaurus
            ?  <Words
                onClickWord={ onClickWord }
                words={ searchThesaurus.words }
            />
            : <Info message="no results found" />
        }
    </div>
);
