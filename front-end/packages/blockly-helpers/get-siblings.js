export default block => (
    block.getSurroundParent()
        ? block.getSurroundParent().getChildren( true )
        : block.workspace.getTopBlocks()
);
