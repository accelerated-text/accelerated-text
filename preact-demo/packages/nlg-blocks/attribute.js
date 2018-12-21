import Block            from './Block';

export default Block({

    type:                   'attribute',

    json: {
        colour:             20,
        output:             null,
        message0:           '%1 attribute',
        args0: [{
            type:           'field_dropdown',
            name:           'attribute_name',
            options: [
                [ 'Color',      'color' ],
                [ 'Material',   'material' ],
                [ 'Make',       'make' ],
            ],
        }],
    },
});
