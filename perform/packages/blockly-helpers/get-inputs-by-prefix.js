export default ( block, prefix ) =>
    block.inputList.filter(
        input => input.name.startsWith( prefix )
    );
