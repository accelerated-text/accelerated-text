import Block                from './Block';


export default Block({

    type:                   'if-then-else',

    json: {
        colour:             164,
        output:             null,
        message0:           'if %1',
        args0: [{
            type:           'input_value',
            name:           'if',
        }],
        message1:           'then %1',
        args1: [{
            type:           'input_value',
            name:           'then',
        }],
        message2:           'else %1',
        args2: [{
            type:           'input_value',
            name:           'else',
        }],
    },
});
