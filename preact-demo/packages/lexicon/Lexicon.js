import { h }            from 'preact';

import EXAMPLE_DATA     from './lexicon-example.json';
import S                from './Lexicon.sass';
import WordLists        from './WordLists';


export default () =>
    <div className={ S.className }>
        <div className={ S.top }>
            <button className={ S.new }>
                ➕ New list
            </button>
            <input
                className={ S.search }
                placeholder="search"
                type="search"
            />
        </div>
        <div className={ S.list }>
            <WordLists lists={ EXAMPLE_DATA.results } />
        </div>
        <button className={ S.more }>
            More results
        </button>
    </div>;
