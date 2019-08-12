import { h }                from 'preact';
import { path }             from 'ramda';

import { composeQueries }   from '../graphql/';

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

    return (
        <div className={ S.className }>
            <img className={ S.thumbnail } src={ record.thumbnail } />
            <h3 children={ record.title } className={ S.title } />
            <p>ISBN: { record['isbn-13'] }</p>
            <p>{ descriptionText }</p>
            { product
                ? <UpdateProduct
                    description={ descriptionText || '' }
                    disabled={ loading }
                    product={ product }
                />
                : <AddProduct
                    description={ descriptionText || '' }
                    disabled={ loading }
                    record={ record }
                />
            }
        </div>
    );
});
