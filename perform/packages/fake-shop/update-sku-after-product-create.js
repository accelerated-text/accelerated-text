import { path }             from 'ramda';
import uuid                 from 'uuid';

import { searchProducts }   from './queries.graphql';


export default ( cache, data ) => {
    const product = path(
        [ 'data', 'productCreate', 'product' ],
        data,
    );
    const sku = path(
        [ 'variants', 'edges', 0, 'node', 'sku' ],
        product,
    );
    if( sku ) {
        cache.writeQuery({
            query:          searchProducts,
            variables: {
                query:      `sku:${ sku }`,
            },
            data: {
                products: {
                    __typename: 'ProductConnection',
                    edges: [{
                        __typename: 'ProductEdge',
                        cursor:     uuid.v4(),
                        node:       product,
                    }],
                },
            },
        });
    }
};
