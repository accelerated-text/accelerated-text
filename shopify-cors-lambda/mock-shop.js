const bodyParser =          require( 'body-parser' );


const MOCK_SHOP_PATH =      '/mock-shop';

const PRODUCTS = {"data":{"products":{"edges":[{"cursor":"eyJsYXN0X2lkIjoyMjY1NTU2ODc3NDA4LCJsYXN0X3ZhbHVlIjoyMjY1NTU2ODc3NDA4fQ==","node":{"id":"gid://shopify/Product/2265556877408","descriptionHtml":"Manu Konchady wrote with Building Search Applications watch with Lucene, LingPipe, and Gate.","title":"Building Search Applications","__typename":"Product","images":{"edges":[{"node":{"id":"gid://shopify/ProductImage/11771933786208","altText":"Building Search Applications","src":"https://cdn.shopify.com/s/files/1/0115/7686/8960/products/content.jpg?v=1565778995","__typename":"Image"},"__typename":"ImageEdge"}],"__typename":"ImageConnection"},"variants":{"edges":[{"node":{"id":"gid://shopify/ProductVariant/20108392661088","sku":"9780615204253","__typename":"ProductVariant"},"__typename":"ProductVariantEdge"}],"__typename":"ProductVariantConnection"}},"__typename":"ProductEdge"}],"__typename":"ProductConnection"}},"extensions":{"cost":{"requestedQueryCost":252,"actualQueryCost":9,"throttleStatus":{"maximumAvailable":1000,"currentlyAvailable":991,"restoreRate":50}}}};


module.exports = app => {

    app.post(
        MOCK_SHOP_PATH,
        bodyParser.text({ type: '*/*' }),
        ( req, res ) => {
            console.log( 'POST', MOCK_SHOP_PATH, req.headers, req.body );
            res.send( PRODUCTS );
        });
};
