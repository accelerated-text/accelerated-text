import {
    flatten,
    sort,
    uniq,
}                           from 'ramda';

import * as NlgTypes        from '../nlg-blocks/types';


const SYNONYMS = {
    examine:       [ 'audit', 'check', 'check out', 'consider', 'criticize', 'delve into', 'explore', 'inspect', 'investigate', 'ponder', 'pore over', 'probe', 'read', 'research', 'review', 'scan', 'screen', 'scrutinize', 'study', 'survey', 'try', 'vet', 'view', 'appraise', 'assay', 'canvass', 'case', 'eye', 'finger', 'frisk', 'gun', 'inquire', 'parse', 'peruse', 'prospect', 'prove', 'reconnoiter', 'sift', 'sweep', 'weigh', 'winnow', 'chew over', 'dig into', 'go into', 'go over', 'go through', 'look over', 'look see', 'pat down', 'pick at', 'scope', 'scrutinate', 'search into', 'size up', 'take stock of', 'turn over' ],
    look:           [ 'consider', 'glance', 'notice', 'peer', 'read', 'see', 'stare', 'study', 'view', 'watch', 'admire', 'attend', 'behold', 'beware', 'contemplate', 'eye', 'flash', 'focus', 'gape', 'gawk', 'gaze', 'glower', 'goggle', 'heed', 'inspect', 'mark', 'mind', 'note', 'observe', 'ogle', 'peep', 'regard', 'rubberneck', 'scan', 'scout', 'scrutinize', 'spot', 'spy', 'survey', 'tend', 'feast one\'s eyes', 'get a load of', 'pore over', 'take a gander', 'take in the sights' ],
    notice:         [ 'acknowledge', 'catch', 'detect', 'discern', 'look at', 'note', 'recognize', 'regard', 'see', 'spot', 'advert', 'allude', 'clock', 'descry', 'dig', 'distinguish', 'espy', 'heed', 'mark', 'mind', 'refer', 'remark', 'flash on', 'get a load of', 'make out', 'pick up on', 'take in' ],
    observe:        [ 'detect', 'discover', 'examine', 'inspect', 'look at', 'mark', 'monitor', 'note', 'pay attention to', 'recognize', 'regard', 'scrutinize', 'study', 'view', 'watch', 'witness', 'beam', 'behold', 'catch', 'contemplate', 'dig', 'discern', 'distinguish', 'espy', 'eyeball', 'flash', 'mind', 'perceive', 'read', 'spot', 'spy', 'survey', 'eagle-eye', 'get a load of', 'get an eyeful of', 'keep one\'s eye on', 'lamp', 'make out', 'pick up on', 'take in' ],
    see:            [ 'detect examine', 'identify', 'look', 'look at', 'notice', 'observe', 'recognize', 'regard', 'spot', 'view', 'watch', 'witness', 'beam', 'behold', 'clock', 'contemplate', 'descry', 'discern', 'distinguish', 'espy', 'eye', 'flash', 'gape', 'gawk', 'gaze', 'glare', 'glimpse', 'heed', 'inspect', 'mark', 'mind', 'note', 'peek', 'peep', 'peer', 'peg', 'penetrate', 'pierce', 'remark', 'scan', 'scope', 'scrutinize', 'sight', 'spy', 'stare', 'survey', 'be apprised of', 'catch a glimpse of', 'catch sight of', 'get a load of', 'lay eyes on', 'make out', 'pay attention to', 'take notice' ],
    view:           [ 'consider', 'examine', 'explore', 'notice', 'observe', 'perceive', 'read', 'regard', 'scrutinize', 'see', 'watch', 'witness', 'beam', 'behold', 'canvass', 'contemplate', 'descry', 'dig', 'discern', 'distinguish', 'espy', 'eye', 'flash', 'gaze', 'inspect', 'mark', 'pipe', 'rubberneck', 'scan', 'scope', 'spot', 'spy', 'stare', 'survey', 'check out', 'check over', 'eagle eye', 'feast eyes on', 'get a load of', 'lay eyes on', 'set eyes on', 'take in' ],
    watch:          [ 'attend', 'check out', 'examine', 'follow', 'keep an eye on', 'listen', 'look', 'observe', 'regard', 'scan', 'scrutinize', 'see', 'stare', 'view', 'wait', 'case', 'concentrate', 'contemplate', 'eye', 'eyeball', 'focus', 'gaze', 'inspect', 'mark', 'mind', 'note', 'peer', 'pipe', 'rubberneck', 'scope', 'spy', 'eagle-eye', 'get a load of', 'give the once over', 'have a look-see', 'keep tabs on', 'pay attention', 'take in', 'take notice' ],
};

const HELP_TEXT = Object.values( SYNONYMS )
    .map( words =>
         `*${ words[0] }* ${ words.join( ' ' ) }`
    ).join( '\n\n' );


export const Organization = () => ({
    __typename:     'Organization',
    id:             'example-org',
    name:           'The Organization',
});

export const Word = text => ({
    __typename:     'Word',
    id:             text,
    partOfSpeech:   'VB',
    text,
});

export const User = () => ({
    __typename:     'User',
    id:             'example-user',
    fullName:       'Example User',
    email:          'example.user@example.org',
    organization:   Organization,
});


export const ThematicRole = ( fieldLabel, fieldType ) => ({
    __typename:     'ThematicRole',
    id:             fieldLabel,
    fieldLabel,
    fieldType,
});


export const Concept = ( label, dictItemName, roles ) => ({
    __typename:     'Concept',
    id:             label,
    label,
    helpText:       HELP_TEXT,
    dictionaryItem: {
        __typename: 'DictionaryItem',
        id:         dictItemName,
        name:       dictItemName,
    },
    roles: Object
        .keys( roles )
        .map( fieldLabel => ThematicRole( fieldLabel, roles[fieldLabel])),
});


export default {
    Organization,
    User,
    Mutation:               {},
    Query: {

        concepts: () => ({
            __typename:     'Concepts',
            id:             'Concepts-id',
            concepts: [
                Concept( 'See', 'sees', {
                    agent:      NlgTypes.TEXT,
                    coAgent:    NlgTypes.TEXT,
                }),
                Concept( 'Arrive', 'arrived', {
                    agent:      NlgTypes.TEXT,
                    atPlace:    NlgTypes.TEXT,
                    onTime:     NlgTypes.TEXT,
                }),
            ],
        }),

        me:                 User,

        quickSearch: ( _, { query }, { cache }) => {
            const re =      new RegExp( `^${ query }`, 'i' );
            const words =   uniq( flatten( Object.values( SYNONYMS )).sort());
            const items =
                words
                    .filter( re.exec.bind( re ))
                    .map( Word );
            return {
                __typename: 'QuickSearchResults',
                offset:     0,
                totalCount: items.length,
                items:      items.slice( 0, 20 ),
            };
        },

        searchThesaurus: ( _, { query }) => {
            const re =      new RegExp( `^${ query }`, 'i' );
            const words = (
                Object.keys( SYNONYMS )
                    .filter( re.exec.bind( re ))
                    .map( Word )
            );
            return {
                __typename: 'ThesaurusResults',
                offset:     0,
                totalCount: words.length,
                words,
            };
        },

        synonyms: ( _, { wordId }) => ({
            __typename:     'Synonyms',
            rootWord:       Word( wordId ),
            synonyms:       ( SYNONYMS[wordId] || []).map( Word ),
        }),
    },
};
