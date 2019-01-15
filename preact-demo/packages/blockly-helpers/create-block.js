export default ( blockType, workspace ) => {
    const block =   workspace.newBlock( blockType );
    block.initSvg();
    return block;
};

