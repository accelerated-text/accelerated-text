export default block =>
    block.inputList
        .map( input => input.fieldRow )
        .reduce(( prevRow, nextRow ) => [ ...prevRow, ...nextRow ])
        .map( field => field.getText())
        .join( ' ' );
