export default block => (
    block.getParent()
        ? block.getParent().getChildren( true )
        : block.workspace.getTopBlocks()
);
