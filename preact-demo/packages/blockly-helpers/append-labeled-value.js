export default ( block, name, label ) => {
    const input =   block.appendValueInput( name );
    input.insertFieldAt( 0, label );
    return input;
}
