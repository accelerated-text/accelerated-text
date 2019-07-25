const blocks = [
    require( './AMR' ),
    require( './And-or' ),
    require( './Cell' ),
    require( './Define-var' ),
    require( './Dictionary-item' ),
    require( './Dictionary-item-modifier' ),
    require( './Document-plan' ),
    require( './Get-var' ),
    require( './If-then-else' ),
    require( './Not' ),
    require( './One-of-synonyms' ),
    require( './Product' ),
    require( './Product-component' ),
    require( './Quote' ),
    require( './Relationship' ),
    require( './Rhetorical' ),
    require( './RST' ),
    require( './Segment' ),
    require( './Sequence' ),
    require( './Shuffle' ),
    require( './Value-comparison' ),
    require( './Value-in' ),
    require( './Xor' ),
];


export const provideBlocks = Blockly =>
    blocks.forEach(
        block => block.default( Blockly )
    );
