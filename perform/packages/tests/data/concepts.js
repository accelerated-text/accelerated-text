import * as NlgTypes        from '../../nlg-blocks/types';


export const HELP_TEXT = `
# Please help me!

I am drowning in code

Help! I need somebody.

Help! Not just anybody.

Won't you please please help me?
`;


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


export const Concepts = concepts => ({
    __typename:     'Concepts',
    id:             'Concepts',
    concepts:       concepts.map( c => Concept( ...c )),
});


export const EMPTY_CONCEPTS = {
    concepts:       Concepts([]),
};


export default {
    concepts: Concepts([
        [ 'See', 'sees', {
            agent:      NlgTypes.TEXT,
            coAgent:    NlgTypes.TEXT,
        }],
        [ 'Arrive', 'arrived', {
            agent:      NlgTypes.TEXT,
            atPlace:    NlgTypes.TEXT,
            onTime:     NlgTypes.TEXT,
        }],
    ]),
};
