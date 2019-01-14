export default ( prevBlock, nextBlock ) => {
    prevBlock.nextConnection.connect( nextBlock.previousConnection );
    return nextBlock;
};
