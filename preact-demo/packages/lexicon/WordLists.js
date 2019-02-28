import { h }            from 'preact';

import S                from './WordLists.sass';
import WordsCell        from './WordsCell';


export default ({ lists }) =>
    <dl className={ S.className } >
        <dt className={ S.wordListHeader }>ID</dt>
        <dd className={ S.wordListHeader }>words</dd>
        { lists.map(({ id, words }) => [
            <dt key={ id }>{ id }</dt>,
            <WordsCell key={ id } words={ words } />,
        ])}
    </dl>;
