import { h }            from 'preact';

import S                from './WordLists.sass';
import WordsCell        from './WordsCell';


export default ({ items }) =>
    <dl className={ S.className } >
        <div className={ S.header }>ID</div>
        <div className={ S.header }>words</div>
        { items.map(({ key, synonyms }) => [
            <dt key={ `K:${ key }` }>{ key }</dt>,
            <WordsCell key={ `V:${ key }` } words={ synonyms } />,
        ])}
    </dl>;
