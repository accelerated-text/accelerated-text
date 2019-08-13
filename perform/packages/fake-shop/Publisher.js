import { h }                from 'preact';
import { path }             from 'ramda';

import { composeQueries }   from '../graphql/';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';
import VariantsView         from '../variants/ViewWithStatus';

import AddProduct           from './AddProduct';
import S                    from './Publisher.sass';
import { searchProducts }   from './queries.graphql';
import UpdateProduct        from './UpdateProduct';


const firstProduct =        path([ 'edges', 0, 'node' ]);


export default composeQueries({
    searchProducts: [
        searchProducts,
        { query:            'query' },
    ],
})(({
    descriptionText,
    record,
    searchProducts: { error, loading, products },
}) => {
    const product =         firstProduct( products );
    const description =     descriptionText || '';
    const isUpToDate = (
        product
        && description
        && product.descriptionHtml === description
    );

    return (
        <div className={ S.className }>
            <img className={ S.thumbnail } src={ record.thumbnail } />
            <h3 children={ record.title } className={ S.title } />
            <p>by <i>{ record.authors }</i></p>
            <p>ISBN: { record['isbn-13'] }</p>
            <VariantsView
                emptyMessage="No description (yet?)"
                loadingMessage="Updating description"
            >
                { () => [
                    <div className={ S.description }>{ description }</div>,
                    isUpToDate
                        ? <Info message="The product is up-to-date" />
                    : error
                        ? <Error message="Error getting product from shop." />
                    : loading
                        ? <Loading message="Getting product from shop" />
                    : product
                        ? <UpdateProduct
                            description={ description }
                            disabled={ loading }
                            product={ product }
                        />
                        : <AddProduct
                            className={ S.addProduct }
                            description={ description }
                            disabled={ loading }
                            record={ record }
                        />,
                ] }
            </VariantsView>
        </div>
    );
});
