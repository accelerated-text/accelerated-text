export default workspace => {
    const svg =         workspace.getParentSvg();

    svg.tabIndex =      '0'; /// Fix keyboard issues with workspace:
    return svg.focus();
};
