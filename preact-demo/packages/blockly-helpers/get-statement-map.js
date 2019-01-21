import getStatements        from './get-statements';
import getThisAndNextBlocks from './get-this-and-next-blocks';

export default block =>
    getStatements( block )
        .map( input => input.name )
        .reduce(
            ( acc, name ) => {
                acc[name] = getThisAndNextBlocks(
                    block.getInputTargetBlock( name )
                );
                return acc;
            },
            {}
        );

