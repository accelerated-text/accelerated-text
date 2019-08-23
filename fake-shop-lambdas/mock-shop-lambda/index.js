/// Constants ------------------------------------------------------------------

const EXTENSIONS = {"cost":{"requestedQueryCost":252,"actualQueryCost":9,"throttleStatus":{"maximumAvailable":1000,"currentlyAvailable":991,"restoreRate":50}}};

const PRODUCT = {"id":"gid://shopify/Product/2265556877408","descriptionHtml":"Manu Konchady wrote with Building Search Applications watch with Lucene, LingPipe, and Gate.","title":"Building Search Applications","__typename":"Product","images":{"edges":[{"node":{"id":"gid://shopify/ProductImage/11771933786208","altText":"Building Search Applications","src":"https://cdn.shopify.com/s/files/1/0115/7686/8960/products/content.jpg?v=1565778995","__typename":"Image"},"__typename":"ImageEdge"}],"__typename":"ImageConnection"},"variants":{"edges":[{"node":{"id":"gid://shopify/ProductVariant/20108392661088","sku":"9780615204253","__typename":"ProductVariant"},"__typename":"ProductVariantEdge"}],"__typename":"ProductVariantConnection"}}

const PRODUCT_SKU =         PRODUCT.variants.edges[0].node.sku;
const PRODUCT_SKU_QUERY =   `sku:${ PRODUCT_SKU }`;

const RESPONSE_HEADERS = {
    'access-control-allow-headers':	'content-type, *',
    'access-control-allow-methods':	'*',
    'access-control-allow-origin':	'*',
    'content-type':                 'application/json',
};


/// Functions ------------------------------------------------------------------

const graphqlResponse = data => ({
    data,
    extensions:             EXTENSIONS,
});

const operations = {

    createProduct: ({
        descriptionHtml,
        /// imageSrc,
        sku,
        title,
    }) => graphqlResponse({
        productCreate: {
            __typename: 'ProductCreatePayload',
            product: {
                ...PRODUCT,
                title,
                descriptionHtml,
                variants: {
                    ...PRODUCT.variants,
                    edges: [{
                        ...PRODUCT.variants.edges[0],
                        node: {
                            ...PRODUCT.variants.edges[0].node,
                            sku,
                        },
                    }],
                },
            },
        },
    }),

    searchProducts: ({ query }) => graphqlResponse({
        products: {
            __typename: 'ProductConnection',
            edges: (
                query === PRODUCT_SKU_QUERY
                    ? [{
                        __typename: 'ProductEdge',
                        cursor:     Math.random().toString( 36 ),
                        node:       PRODUCT,
                    }]
                    : []
            ),
        },
    }),

    updateProduct: ({ descriptionHtml }) => graphqlResponse({
        productUpdate: {
            __typename: 'ProductUpdatePayload',
            product: {
                ...PRODUCT,
                descriptionHtml,
            },
        },
    }),
};


/// Handler --------------------------------------------------------------------

exports.handler = ( event, _, callback ) => {
    console.log( 'EVENT', typeof event.body, event.headers );

    let bodyJson =          null;
    try {
        bodyJson =          JSON.parse( event.body );
        const operation =   operations[ bodyJson.operationName ];
        callback( null, {
            isBase64Encoded:    false,
            statusCode:         200,
            body:               JSON.stringify( operation( bodyJson.variables )),
            headers:            RESPONSE_HEADERS,
        });
    } catch( err ) {
        callback( err );
    }
};
