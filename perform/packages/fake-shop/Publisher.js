import { h }                from 'preact';
import { path }             from 'ramda';

import { composeQueries }   from '../graphql/';
import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';

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
    descriptionError,
    descriptionLoading,
    descriptionText,
    record,
    searchProducts: { error, loading, products },
}) => {
    const product =         firstProduct( products );
    const description = (
        ! descriptionError
        && ! descriptionLoading
        && descriptionText
        || ''
    );
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
            <div className={ S.description }>{
                descriptionError
                    ? <Error message="Error generating description" />
                : descriptionLoading
                    ? <Loading message="Updating description" />
                : description
                    ? description
                    : <Info message="No description (yet?)" />
            }</div>
            { isUpToDate
                ? <p><Info message="The product is up-to-date" /></p>
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
                />
            }
        </div>
    );
});
