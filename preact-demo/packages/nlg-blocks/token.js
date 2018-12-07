import Block            from './Block';

export default Block({

    type:                   'token',

    json: {
        colour:             270,
        output:             null,
        message0:           '%1 (%2)',
        args0: [{
            type:           'field_input',
            name:           'text',
            text:           'default text',
        }, {
            type:           'field_dropdown',
            name:           'part_of_speech',
            options: [
                [ 'Adjective',      'ADJ' ],
                [ 'Det.',           'DET' ],
                [ 'Name',           'PROPN' ],
                [ 'Noun',           'NOUN' ],
                [ 'Punctuation',    'PUNCT' ],
                [ 'Verb',           'VERB' ],
            ],
        }],
    },
});
