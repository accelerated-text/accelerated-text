import classnames               from 'classnames';
import { h }                    from 'preact';

import { composeQueries }       from '../graphql';
import { searchPhrases }        from '../graphql/queries.graphql';

import Result                   from './Result';
import S                        from './SearchResults.sass';


export default composeQueries({
    searchPhrases:      [ searchPhrases, { query: 'query' }],
})(({
    className,
    searchPhrases: { searchPhrases },
}) =>
    <ol className={ classnames( S.className, className ) }>
        { searchPhrases && searchPhrases.map(
            ({ id, text }) =>
                <Result key={ id } id={ id } text={ text } />
        )}
    </ol>
);
