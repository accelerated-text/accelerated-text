import getThisAndNextBlocks from './get-this-and-next-blocks';


export default block =>
    getThisAndNextBlocks( block.getNextBlock());
