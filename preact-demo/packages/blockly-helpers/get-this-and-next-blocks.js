const getThisAndNextBlocks = block =>
    block
        ? [ block, ...getThisAndNextBlocks( block.getNextBlock()) ]
        : [];

export default getThisAndNextBlocks;
