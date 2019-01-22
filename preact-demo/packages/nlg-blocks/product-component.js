import Block                from './Block';
import product              from './product';


export default Block({
    ...product,

    type:                   'product-component',

    json: {
        ...product.json,

        message0:           'Component',
    },
});
