import { blocks as B }      from '../nlg-blocks/';
import phrasesToString      from '../dictionary/phrases-to-string';
import posToString          from '../part-of-speech/to-string';


const BLOCK_RESULTS = {
    'AND / OR':                     B.AndOr,
    'EITHER - OR':                  B.Xor,
    'modifier':                     B.Modifier,
    'define variable':              B.DefineVar,
    'if..then..else':               B.IfThenElse,
    'one of synonyms':              B.OneOfSynonyms,
    'sequence of texts':            B.Sequence,
    'shuffled texts':               B.Shuffle,
    'text segment':                 B.Segment,
    'use variable':                 B.GetVar,
    'value comparison (= / > / <)': B.ValueComparison,
    'value in list':                B.ValueIn,
    NOT:                            B.Not,
};

const searchBlocks = query => {
    const re =              new RegExp( `\\b${ query }`, 'i' );
    return Object.entries( BLOCK_RESULTS )
        .filter(([ key ]) =>
            query ? re.exec( key ) : true
        ).map(([ text, type ]) => ({
            text:           `🧩 ${ text }`,
            types:          [ type ],
        }));
};

const createQuoteResult = text => [{
    text,
    types:                  [ B.Quote, B.DictionaryItem, B.DictionaryItemModifier, B.DefineVar ],
}];


const searchVars = ( query, workspace ) => {
    const re =              new RegExp( `\\b${ query }`, 'i' );
    return workspace.getVariableMap().getAllVariables()
        .filter( varModel => re.exec( varModel.name ))
        .map( varModel => ({
            text:           varModel.name,
            details:        varModel.type,
            types:          [ B.GetVar ],
        }));
};


const recordFieldToResult = field => ({
    text:                   field.fieldName,
    details:                field.value,
    types:                  [ B.Cell, B.CellModifier, B.Quote ],
});

const searchCells = ( query, file, plan ) => {
    const re =              new RegExp( `\\b${ query }`, 'i' );
    return (
        plan
        && file
        && file.records
        && file.records[ plan.dataSampleRow ]
        && file.records[ plan.dataSampleRow ].fields
        && file.records[ plan.dataSampleRow ].fields
            .filter( field => re.exec( field.fieldName ))
            .sort(( a, b ) => a.fieldName > b.fieldName ? 1 : -1 )
            .map( recordFieldToResult )
        || []
    );
};


const wordToResult = word => ({
    text:                   word.text,
    details:                posToString( word.partOfSpeech ),
    partOfSpeech:           word.partOfSpeech,
    concept:                word.concept,
    types: [
        word.concept && B.AMR,
        B.Quote,
        B.DictionaryItem,
        B.DictionaryItemModifier,
    ].filter( Boolean ),
});

const thesaurusResults = searchThesaurus => (
    searchThesaurus
    && searchThesaurus.words
    && searchThesaurus.words.map( wordToResult )
    || []
);

const dictionaryItemToResult = item => ({
    dictionaryItemId:       item.id,
    text:                   item.name,
    details:                `${ posToString( item.partOfSpeech )}: ${ phrasesToString( item.phrases )}`,
    concept:                item.concept,
    types: [
        item.concept && B.AMR,
        B.DictionaryItem,
        B.DictionaryItemModifier,
        B.Quote,
    ].filter( Boolean ),
});

const searchDictionary = ( query, dictionary ) => {
    const re =              new RegExp( `^${ query }`, 'i' );
    return (
        dictionary
        && dictionary.items
        && dictionary.items
            .filter( item => re.exec( item.name ))
            .sort(( a, b ) => a.name > b.name ? 1 : -1 )
            .map( dictionaryItemToResult )
        || []
    );
};


export default ({
    dictionary,
    file,
    filterTypes,
    plan,
    query,
    searchThesaurus,
    sortTypes,
    workspace,
}) => ([
    ...( query && createQuoteResult( query ) || []),
    ...searchBlocks( query ),
    ...searchVars( query, workspace ),
    ...searchCells( query, file, plan ),
    ...searchDictionary( query, dictionary ),
    ...thesaurusResults( searchThesaurus ),
].map( result => ({
    ...result,
    types:                  result.types.filter( filterTypes ),
})).filter( result =>
    result.types.length
).slice( 0, 20
).map( result => ({
    ...result,
    types:              result.types.sort( sortTypes ),
})));
