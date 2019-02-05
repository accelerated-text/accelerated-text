export default block =>
    block.inputList
        .filter( input => input.type === Blockly.NEXT_STATEMENT );
