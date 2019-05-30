export const Organization = () => ({
    __typename:     'Organization',
    id:             'example-org',
    name:           'The Organization',
});
export const Phrase = text => ({
    __typename:     'Phrase',
    id:             text,
    text,
});
export const ReaderFlag = name => ({
    __typename:     'ReaderFlag',
    id:             name,
    name,
    somethingElse:  'test',
});
export const User = () => ({
    __typename:     'User',
    id:             'example-user',
    fullName:       'Example User',
    email:          'example.user@example.org',
    organization:   Organization,
});

export default {
    Organization,
    User,
    Query: {
        me:             User,
        phrases:        ( _, { query = 'phrase' }) => [ Phrase( query ) ],
        readerFlags:    () => [ ReaderFlag( 'junior' ), ReaderFlag( 'senior' ) ],
    },
};
