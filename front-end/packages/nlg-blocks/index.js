export const blocks = {
    AMR:                    require( './AMR' ).default,
    AndOr:                  require( './And-or' ).default,
    Cell:                   require( './Cell' ).default,
    CellModifier:           require( './Cell-modifier' ).default,
    DefineVar:              require( './Define-var' ).default,
    DictionaryItem:         require( './Dictionary-item' ).default,
    DictionaryItemModifier: require( './Dictionary-item-modifier' ).default,
    DocumentPlan:           require( './Document-plan' ).default,
    GetVar:                 require( './Get-var' ).default,
    IfThenElse:             require( './If-then-else' ).default,
    Modifier:               require( './Modifier' ).default,
    Not:                    require( './Not' ).default,
    OneOfSynonyms:          require( './One-of-synonyms' ).default,
    Quote:                  require( './Quote' ).default,
    //Segment:                require( './Segment' ).default,
    //RglFrame:               require( './RglFrame' ).default,
    //Frame:                  require( './Frame' ).default,
    Sequence:               require( './Sequence' ).default,
    Shuffle:                require( './Shuffle' ).default,
    ValueComparison:        require( './Value-comparison' ).default,
    ValueIn:                require( './Value-in' ).default,
    Xor:                    require( './Xor' ).default,
};

export const DocumentPlanBlocks = {
    Segment:                require( './Segment' ).default,
};


export const RglBlocks = {
    Segment:               require( './RglFrame' ).default,
};

export const AmrBlocks = {
    Segment:               require( './Frame' ).default,
};

export const provideDocumentPlanBlocks = Blockly =>
    Object.values( Object.assign({}, blocks, DocumentPlanBlocks ))
        .forEach( block => block( Blockly ));

export const provideRglBlocks = Blockly =>
    Object.values( Object.assign({}, blocks, RglBlocks ))
        .forEach( block => block( Blockly ));

export const provideAmrBlocks = Blockly =>
    Object.values( Object.assign({}, blocks, AmrBlocks ))
        .forEach( block => block( Blockly ));
