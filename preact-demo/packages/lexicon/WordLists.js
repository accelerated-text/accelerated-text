import { h }            from 'preact';

import S                from './WordLists.sass';
import WordsCell        from './WordsCell';


export default ({ lists }) =>
    <dl className={ S.className } >
        <div className={ S.header }>ID</div>
        <div className={ S.header }>words</div>
        { lists.map(({ id, words }) => [
            <dt key={ id }>{ id }</dt>,
            <WordsCell key={ id } words={ words } />,
        ])}
    </dl>;
