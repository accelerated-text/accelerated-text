const blocks = [
    require( './all' ),
    require( './any-count-from' ),
    require( './attribute' ),
    require( './quote' ),
    require( './rhetorical' ),
    require( './segment' ),
    require( './sequence' ),
    require( './value-list-statement' ),
];


export const provideBlocks = Blockly =>
    blocks.forEach(
        block => block.default( Blockly )
    );
