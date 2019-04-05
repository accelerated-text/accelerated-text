export default block =>
    block.inputList.filter(
        input => input.type === Blockly.INPUT_VALUE
    );
