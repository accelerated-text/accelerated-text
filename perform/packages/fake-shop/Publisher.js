import { h }                from 'preact';

import { composeQueries }   from '../graphql/';

import S                    from './Publisher.sass';
import { searchProducts }   from './queries.graphql';


export default composeQueries({
    searchProducts: [
        searchProducts,
        { query:            'query' },
    ],
})(({
    descriptionText,
    record,
    searchProducts: { error, loading, searchProducts },
}) =>
    <div className={ S.className }>
        { record && [
            <img className={ S.thumbnail } src={ record.thumbnail } />,
            <h3 children={ record.title } className={ S.title } />,
            <p>ISBN: { record['isbn-13'] }</p>,
            <p>{ descriptionText }</p>,
            <button children="Create product" />,
        ]}
        <p>Error: { JSON.stringify( error ) }</p>
        <p>Loading: { JSON.stringify( loading ) }</p>
        <p>Result: { JSON.stringify( searchProducts ) }</p>
    </div>
);
