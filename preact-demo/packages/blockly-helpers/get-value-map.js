import getValueInputs   from './get-value-inputs';

export default block =>
    getValueInputs( block )
        .reduce(
            ( acc, input ) => {
                const valueBlock = block.getInputTargetBlock( input.name );
                if( valueBlock ) {
                    acc[input.name] = valueBlock;
                }
                return acc;
            },
            {}
        );
