export default Blockly =>
    Blockly.Blocks.segment = {
        init() {
            this.jsonInit({
                type:               'json_repeat_ext',
                colour:             30,
                nextStatement:      'Action',
                previousStatement:  'Action',
                message0:           '%1 Segment',
                args0: [{
                    type:           'field_dropdown',
                    name:           'GOAL',
                    options: [
                        [ 'description', 'Description' ],
                        [ 'pitch', 'Pitch' ],
                    ],
                }],
                message1:           'with %1',
                args1: [{
                    type:           'input_statement',
                    name:           'CHILDREN',
                }],
            });
        },
    };
