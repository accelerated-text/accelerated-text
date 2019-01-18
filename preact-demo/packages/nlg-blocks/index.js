const blocks = [
    require( './all' ),
    require( './and-or' ),
    require( './any-count-from' ),
    require( './Cell' ),
    require( './Document-plan' ),
    require( './if-then-else' ),
    require( './not' ),
    require( './number-comparison' ),
    require( './product' ),
    require( './product-component' ),
    require( './quote' ),
    require( './relationship' ),
    require( './rhetorical' ),
    require( './segment' ),
    require( './sequence' ),
    require( './value-in-list' ),
    require( './xor' ),
];


export const provideBlocks = Blockly =>
    blocks.forEach(
        block => block.default( Blockly )
    );
