import { flatten }          from 'ramda';


export const getBlocksWithDistances = ( originBlock, distance ) => block => {
    const proceed =         getBlocksWithDistances( block, distance + 1 );
    const parent =          block.getParent();
    return [
        { block, distance },
        ...block.getChildren()
            .filter( child => child !== originBlock )
            .map( proceed ),
        ( parent && parent !== originBlock )
            ? proceed( parent )
            : null,
    ];
};


export default ( refBlock, workspace ) => {
    const topBlocks =           workspace.getTopBlocks();

    if( ! refBlock ) {
        return flatten(
            topBlocks.map( getBlocksWithDistances( null, 0 ))
        ).filter( Boolean );
    } else {
        const rootBlock =       refBlock.getRootBlock();
        const refRelatives =    flatten( getBlocksWithDistances( null, 0 )( refBlock ));
        const rootPoint =       refRelatives.find(({ block }) => block === rootBlock );
        return flatten([
            refRelatives,
            ...topBlocks
                .filter( block => block !== rootBlock )
                .map( getBlocksWithDistances( null, rootPoint.distance || 0 )),
        ]).filter( Boolean );
    }
};
