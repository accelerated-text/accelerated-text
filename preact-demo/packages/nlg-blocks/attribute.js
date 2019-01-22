import Block            from './Block';


export default Block({

    type:                   'attribute',

    json: {
        colour:             26,
        output:             null,
        message0:           '%1 attribute',
        args0: [{
            type:           'field_dropdown',
            name:           'attribute_name',
            options: [
                [ 'Color',              'Color' ],
                [ 'Lacing',             'Lacing' ],
                [ 'Main feature',       'Main Feature' ],
                [ 'Make',               'Make' ],
                [ 'Material',           'Material' ],
                [ 'Name',               'Name' ],
                [ 'Product name',       'Product name' ],
                [ 'Style',              'Style' ],
                [ 'Secondary feature',  'Secondary feature' ],
                [ 'Title',              'Title' ],
            ],
        }],
    },
});
