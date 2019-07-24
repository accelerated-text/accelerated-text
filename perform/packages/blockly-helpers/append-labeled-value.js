export default ( block, name, label = '', check = null ) => {
    const input =   block.appendValueInput( name );
    label && input.insertFieldAt( 0, label );
    check && input.setCheck( check );
    return input;
};
