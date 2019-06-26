const DONT_CARE =       'DONT_CARE';
const NO =              'NO';
const YES =             'YES';


export const READER_FLAGS = [
    'test-flag-1',
    'test-flag-2',
];


export const ReaderFlag = name => ({
    __typename:         'ReaderFlag',
    id:                 `${ name }-id`,
    name,
});

export const ReaderFlagUsage = ({ prefix, flag, usage }) => ({
    __typename:         'ReaderFlagUsage',
    id:                 `${ prefix }-${ flag }-usage-id`,
    flag:               ReaderFlag( flag ),
    usage,
});

export const PhraseUsage = ({ prefix, phrase, defaultUsage, readerUsage }) => ({
    __typename:         'PhraseUsageModel',
    id:                 `${ prefix }-phrase-${ phrase }-usage-id`,
    phrase,
    defaultUsage,
    readerUsage: Object.keys( readerUsage ).map(
        flag => ReaderFlagUsage({
            prefix:     `${ prefix }-phrase-${ phrase }`,
            flag,
            usage:      readerUsage[flag],
        })
    ),
});


export const DictionaryItem = ({ prefix, name, phraseUsage }) => ({
    __typename:         'DictionaryItem',
    id:                 `${ prefix }-${ name }-id`,
    name,
    usageModels: phraseUsage.map(
        item => PhraseUsage({
            prefix:         `${ prefix }-${ name }`,
            phrase:         item[0],
            defaultUsage:   item[1],
            readerUsage:    item[2],
        })
    ),
});


export const DictionaryResults = ( prefix, items ) => {

    const names =           Object.keys( items );

    return {
        __typename:         'DictionaryResults',
        limit:              names.length,
        offset:             0,
        totalCount:         names.length,
        items: names.map(
            name => DictionaryItem({
                prefix,
                name,
                phraseUsage:    items[name],
            })
        ),
    };
};


export const EMPTY_DICTIONARY = {
    dictionary:             DictionaryResults( 'empty', {}),
};


export default {
    dictionary: DictionaryResults( 'default', {
        one: [
            [ 'one', YES, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  DONT_CARE,
            }],
            [ 'uno', NO, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  YES,
            }],
            [ 'viens', YES, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  DONT_CARE,
            }],
        ],
        two: [
            [ 'two', NO, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  YES,
            }],
            [ 'zwei', YES, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  NO,
            }],
        ],
        three: [
            [ 'dry', YES, {
                [READER_FLAGS[0]]:  DONT_CARE,
                [READER_FLAGS[1]]:  NO,
            }],
        ],
    }),
};
