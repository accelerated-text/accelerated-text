import Block            from './Block';

export default Block({

    type:                   'attribute',

    json: {
        colour:             120,
        output:             null,
        message0:           '%1 attribute',
        args0: [{
            type:           'field_dropdown',
            name:           'name',
            options: [
                [ 'Color',      'color' ],
                [ 'Material',   'material' ],
                [ 'Make',       'make' ],
            ],
        }],
    },
});
