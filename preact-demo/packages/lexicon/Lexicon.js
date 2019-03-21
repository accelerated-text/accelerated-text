import { h, Component } from 'preact';

import {
    Error,
    Loading,
}   from '../ui-messages/';
import { useStores }    from '../vesa/';

import S                from './Lexicon.sass';
import WordLists        from './WordLists';


export default useStores([
    'lexicon',
])( class Lexicon extends Component {

    onChangeSearch = evt =>
        this.props.E.lexicon.onChangeQuery( evt.target.value );

    render({
        E,
        lexicon: {
            items,
            query,
            resultsError,
            resultsLoading,
            totalCount,
        },
    }) {
        const hasMore = totalCount > items.length;

        return (
            <div className={ S.className }>
                <div className={ S.top }>
                    <button className={ S.new }>
                        ➕ New list
                    </button>
                    <input
                        className={ S.search }
                        onInput={ this.onChangeSearch }
                        placeholder="search"
                        type="search"
                        value={ query }
                    />
                </div>
                { resultsError &&
                    <Error message={ resultsError } />
                }
                { resultsLoading &&
                    <Loading message="Loading..." />
                }
                { items &&
                    <div className={ S.list }>
                        <WordLists items={ items } />
                    </div>
                }
                { hasMore &&
                    <button className={ S.more } onClick={ E.lexicon.onClickMore }>
                        More results
                    </button>
                }
            </div>
        );
    }
});
