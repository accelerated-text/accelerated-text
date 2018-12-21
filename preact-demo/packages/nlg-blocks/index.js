const blocks = [
    require( './all-words' ),
    require( './attribute' ),
    require( './quote' ),
    require( './segment' ),
    require( './sentence' ),
    require( './token' ),
    require( './value-list-statement' ),
];


export const provideBlocks = Blockly =>
    blocks.forEach(
        block => block.default( Blockly )
    );
