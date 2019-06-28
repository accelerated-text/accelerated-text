export const READER_FLAGS = [
    'test-flag-1',
    'test-flag-2',
];


export const ReaderFlags = flags => ({
    readerFlags: {
        __typename: 'ReaderFlags',
        id:         'reader-flags-id',
        flags:      [],
    },
});


export const EMPTY_RFLAGS = ReaderFlags([]);


export default ReaderFlags( READER_FLAGS );
