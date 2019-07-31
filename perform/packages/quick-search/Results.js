import { h }                from 'preact';

import { composeQueries }   from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { quickSearch }      from '../graphql/queries.graphql';

import Item                 from './Item';
import S                    from './Results.sass';


export default composeQueries({
    quickSearch:            [ quickSearch, { query: 'query' }],
})(({
    onSelect,
    query,
    quickSearch: { error, loading, quickSearch },
    withWorkspace,
}) =>
    <ul className={ S.className }>
        { error
            ? <Error message={ error } />
        : loading
            ? <Loading />
        : quickSearch
            ? quickSearch.words.map( item =>
                <Item
                    children={ item.text }
                    item={ item }
                    key={ item.id }
                    onSelect={ onSelect }
                />
            )
            : <Info message="no results found" />
        }
    </ul>
);
