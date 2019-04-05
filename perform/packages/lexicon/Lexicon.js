import classnames       from 'classnames';
import { h, Component } from 'preact';

import {
    Error,
    Loading,
}   from '../ui-messages/';
import { QA }           from '../tests/constants';
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
        const showList =        newItem || items && items.length;

        return (
            <div className={ S.className }>
                <div className={ S.top }>
                    <button
                        children="➕ New list"
                        className={ classnames( S.new, QA.LEXICON_NEW_BTN ) }
                        onClick={ E.lexicon.onClickNew }
                    />
                    <input
                        className={ classnames( S.search, QA.LEXICON_SEARCH ) }
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
                { showList &&
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
                        className={ classnames( S.more, QA.LEXICON_MORE ) }
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
