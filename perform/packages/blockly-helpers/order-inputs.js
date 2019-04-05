export default ( block, inputs ) =>
    inputs
        .reverse()
        .reduce(( nextInput, prevInput ) => {
            block.moveInputBefore(
                prevInput.name,
                nextInput.name,
            );
            return prevInput;
        });
