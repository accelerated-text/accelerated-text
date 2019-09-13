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


export default ( refBlock, workspace ) =>
    flatten(
        refBlock
            ? getBlocksWithDistances( null, 0 )( refBlock )
            : workspace.getTopBlocks().map( getBlocksWithDistances( null, 0 ))
    ).filter( Boolean );
