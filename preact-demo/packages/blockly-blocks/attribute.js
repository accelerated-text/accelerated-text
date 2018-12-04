export default Blockly =>
    Blockly.Blocks.attribute = {
        init() {
            this.jsonInit({
                type:       'example_variable',
                colour:     120,
                nextStatement:      'Action',
                previousStatement:  'Action',
                message0:    'Attribute %1',
                args0: [{
                    type:   'field_dropdown',
                    name:   'ATTRIBUTE',
                    options: [
                        [ 'color',  'Color' ],
                        [ 'material', 'Material' ],
                        [ 'make', 'Make' ],
                    ],
                }],
            });
        },
    };
