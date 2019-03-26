import { h, Component } from 'preact';

import {
    Error,
    Loading,
}   from '../ui-messages/';
import { useStores }    from '../vesa/';

import S                from './Lexicon.sass';
import ItemList         from './ItemList';


export default useStores([
    'lexicon',
])( class Lexicon extends Component {

    onChangeSearch = evt =>
        this.props.E.lexicon.onChangeQuery( evt.target.value );

    render({
        E,
        lexicon: {
            items,
            newItem,
            newItemSaved,
            query,
            requestOffset,
            resultsError,
            resultsLoading,
            totalCount,
        },
    }) {
        const hasMore =         totalCount > items.length;
        const isMoreLoading =   hasMore && resultsLoading && requestOffset;

        return (
            <div className={ S.className }>
                <div className={ S.top }>
                    <button className={ S.new } onClick={ E.lexicon.onClickNew }>
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
                    <ItemList
                        items={ items }
                        newItem={ newItem }
                        newItemSaved={ newItemSaved }
                        onCancelNew={ E.lexicon.onCancelNew }
                        onSaveNew={ E.lexicon.onSaveNew }
                    />
                }
                { hasMore &&
                    <button
                        className={ S.more }
                        disabled={ isMoreLoading }
                        onClick={ E.lexicon.onClickMore }
                    >
                        { isMoreLoading
                            ? <Loading message="Loading more..." />
                            : 'More results'
                        }
                    </button>
                }
            </div>
        );
    }
});
