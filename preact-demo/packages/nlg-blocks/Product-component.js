import Block                from './Block';
import Product              from './Product';


export default Block({
    ...Product,

    type:                   'Product-component',

    json: {
        ...Product.json,

        message0:           'Component named: %1',
    },
});
