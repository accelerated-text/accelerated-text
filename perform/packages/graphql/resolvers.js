import {
    flatten,
    keys,
    values,
}   from 'ramda';


const READER_FLAGS = [ 'junior', 'senior' ];

const SYNONYMS = {
    examine:       [ 'audit', 'check', 'check out', 'consider', 'criticize', 'delve into', 'explore', 'inspect', 'investigate', 'ponder', 'pore over', 'probe', 'read', 'research', 'review', 'scan', 'screen', 'scrutinize', 'study', 'survey', 'try', 'vet', 'view', 'appraise', 'assay', 'canvass', 'case', 'eye', 'finger', 'frisk', 'gun', 'inquire', 'parse', 'peruse', 'prospect', 'prove', 'reconnoiter', 'sift', 'sweep', 'weigh', 'winnow', 'chew over', 'dig into', 'go into', 'go over', 'go through', 'look over', 'look see', 'pat down', 'pick at', 'scope', 'scrutinate', 'search into', 'size up', 'take stock of', 'turn over' ],
    look:           [ 'consider', 'glance', 'notice', 'peer', 'read', 'see', 'stare', 'study', 'view', 'watch', 'admire', 'attend', 'behold', 'beware', 'contemplate', 'eye', 'flash', 'focus', 'gape', 'gawk', 'gaze', 'glower', 'goggle', 'heed', 'inspect', 'mark', 'mind', 'note', 'observe', 'ogle', 'peep', 'regard', 'rubberneck', 'scan', 'scout', 'scrutinize', 'spot', 'spy', 'survey', 'tend', 'feast one\'s eyes', 'get a load of', 'pore over', 'take a gander', 'take in the sights' ],
    notice:         [ 'acknowledge', 'catch', 'detect', 'discern', 'look at', 'note', 'recognize', 'regard', 'see', 'spot', 'advert', 'allude', 'clock', 'descry', 'dig', 'distinguish', 'espy', 'heed', 'mark', 'mind', 'refer', 'remark', 'flash on', 'get a load of', 'make out', 'pick up on', 'take in' ],
    observe:        [ 'detect', 'discover', 'examine', 'inspect', 'look at', 'mark', 'monitor', 'note', 'pay attention to', 'recognize', 'regard', 'scrutinize', 'study', 'view', 'watch', 'witness', 'beam', 'behold', 'catch', 'contemplate', 'dig', 'discern', 'distinguish', 'espy', 'eyeball', 'flash', 'mind', 'perceive', 'read', 'spot', 'spy', 'survey', 'eagle-eye', 'get a load of', 'get an eyeful of', 'keep one\'s eye on', 'lamp', 'make out', 'pick up on', 'take in' ],
    see:            [ 'detect examine', 'identify', 'look', 'look at', 'notice', 'observe', 'recognize', 'regard', 'spot', 'view', 'watch', 'witness', 'beam', 'behold', 'clock', 'contemplate', 'descry', 'discern', 'distinguish', 'espy', 'eye', 'flash', 'gape', 'gawk', 'gaze', 'glare', 'glimpse', 'heed', 'inspect', 'mark', 'mind', 'note', 'peek', 'peep', 'peer', 'peg', 'penetrate', 'pierce', 'remark', 'scan', 'scope', 'scrutinize', 'sight', 'spy', 'stare', 'survey', 'be apprised of', 'catch a glimpse of', 'catch sight of', 'get a load of', 'lay eyes on', 'make out', 'pay attention to', 'take notice' ],
    view:           [ 'consider', 'examine', 'explore', 'notice', 'observe', 'perceive', 'read', 'regard', 'scrutinize', 'see', 'watch', 'witness', 'beam', 'behold', 'canvass', 'contemplate', 'descry', 'dig', 'discern', 'distinguish', 'espy', 'eye', 'flash', 'gaze', 'inspect', 'mark', 'pipe', 'rubberneck', 'scan', 'scope', 'spot', 'spy', 'stare', 'survey', 'check out', 'check over', 'eagle eye', 'feast eyes on', 'get a load of', 'lay eyes on', 'set eyes on', 'take in' ],
    watch:          [ 'attend', 'check out', 'examine', 'follow', 'keep an eye on', 'listen', 'look', 'observe', 'regard', 'scan', 'scrutinize', 'see', 'stare', 'view', 'wait', 'case', 'concentrate', 'contemplate', 'eye', 'eyeball', 'focus', 'gaze', 'inspect', 'mark', 'mind', 'note', 'peer', 'pipe', 'rubberneck', 'scope', 'spy', 'eagle-eye', 'get a load of', 'give the once over', 'have a look-see', 'keep tabs on', 'pay attention', 'take in', 'take notice' ],
};

const USAGE = {
    YES:            'YES',
    NO:             'NO',
    DONT_CARE:      'DONT_CARE',
};

const DICTIONARY = {
    see:    [
        [ 'see',            USAGE.YES,          USAGE.DONT_CARE,    USAGE.DONT_CARE ],
        [ 'examine',        USAGE.YES,          USAGE.NO,           USAGE.DONT_CARE ],
        [ 'look',           USAGE.YES,          USAGE.DONT_CARE,    USAGE.DONT_CARE ],
        [ 'watch',          USAGE.YES,          USAGE.DONT_CARE,    USAGE.DONT_CARE ],
        [ 'check out',      USAGE.NO,           USAGE.YES,          USAGE.DONT_CARE ],
        [ 'contemplate',    USAGE.DONT_CARE,    USAGE.DONT_CARE,    USAGE.YES ],
    ],
};


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
});
export const User = () => ({
    __typename:     'User',
    id:             'example-user',
    fullName:       'Example User',
    email:          'example.user@example.org',
    organization:   Organization,
});

export const DictionaryItem = ( name, usageModels = []) => ({
    __typename:     'DictionaryItem',
    id:             name,
    name,
    usageModels,
});

export const PhraseUsageModel = ( phrase, defaultUsage, readerUsage = []) => ({
    __typename:     'PhraseUsageModel',
    phrase,
    defaultUsage,
    readerUsage,
});

export const ReaderFlagUsage = ( flag, usage ) => ({
    __typename:     'ReaderFlagUsage',
    flag,
    usage,
});


export const dictionaryItem = ( _, { id }) => (
    DICTIONARY[id]
        ?  DictionaryItem(
            id,
            DICTIONARY[id].map( row =>
                PhraseUsageModel(
                    Phrase( row[0]),
                    ReaderFlagUsage( ReaderFlag( 'default' ), row[1]),
                    row.slice( 2 ).map(( usage, i ) =>
                        ReaderFlagUsage( ReaderFlag( READER_FLAGS[i]), usage )
                    ),
                )
            ),
        )
        : null
);

export const dictionary = _root =>
    Object.keys( DICTIONARY )
        .map( id => dictionaryItem( _root, { id }));


export default {
    Organization,
    User,
    Mutation: {
        updatePhraseUsageModelDefault: ( _, { id, defaultUsage }, { cache, getCacheKey }) => {
            const __typename =  'PhraseUsageModel';

            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { defaultUsage },
            });
        },
        updateReaderFlagUsage: ( _, { id, usage }, { cache, getCacheKey }) => {
            const __typename =  'ReaderFlagUsage';

            cache.writeData({
                id:     getCacheKey({ __typename, id }),
                data:   { usage },
            });
        },
    },
    Query: {
        me:             User,
        dictionary,
        dictionaryItem,
        phrases:        ( _, { query }) => {

            const regexp =  new RegExp( `${ query }.*`, 'i' );

            return flatten([ keys( SYNONYMS ), values( SYNONYMS ) ])
                .find( regexp.test.bind( regexp ))
                .map( Phrase );
        },
        readerFlags:    () => READER_FLAGS.map( ReaderFlag ),
        searchPhrases:  ( _, { query }) => {
            const re =  new RegExp( `^${ query }`, 'i' );
            return Object.keys( SYNONYMS )
                .filter( re.exec.bind( re ))
                .map( Phrase );
        },
        synonyms:       ( _, { phraseId }) => ({
            __typename: 'Synonyms',
            rootPhrase: Phrase( phraseId ),
            phrases:    ( SYNONYMS[phraseId] || []).map( Phrase ),
        }),
    },
};
