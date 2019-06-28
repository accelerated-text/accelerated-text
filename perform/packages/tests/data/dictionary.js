import { DONT_CARE, NO, YES }   from './usage';
import { READER_FLAGS }         from './reader-flags';


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

export const Phrase = ({ prefix, text, defaultUsage, readerFlagUsage }) => ({
    __typename:         'Phrase',
    id:                 `${ prefix }-phrase-${ text }-usage-id`,
    text,
    defaultUsage,
    readerFlagUsage: Object.keys( readerFlagUsage ).map(
        flag => ReaderFlagUsage({
            prefix:     `${ prefix }-phrase-${ text }`,
            flag,
            usage:      readerFlagUsage[flag],
        })
    ),
});


export const DictionaryItem = ({ prefix, name, phrases }) => ({
    __typename:         'DictionaryItem',
    id:                 `${ prefix }-${ name }-id`,
    name,
    partOfSpeech:       'VB',
    phrases: phrases.map(
        phrase => Phrase({
            prefix:             `${ prefix }-${ name }`,
            text:               phrase[0],
            defaultUsage:       phrase[1],
            readerFlagUsage:    phrase[2],
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
                phrases:    items[name],
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
