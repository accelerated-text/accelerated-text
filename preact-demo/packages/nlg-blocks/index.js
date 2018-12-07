const blocks = [
    require( './all-words' ),
    require( './attribute' ),
    require( './segment' ),
    require( './sentence' ),
    require( './token' ),
    require( './value-list-statement' ),
];


export const provide = Blockly =>
    blocks.forEach(
        block => block.default( Blockly )
    );
