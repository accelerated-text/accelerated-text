export default block =>
    block.inputList
        .map( input => input.fieldRow )
        .reduce(( prevRow, nextRow ) => [ ...prevRow, ...nextRow ])
        .filter( field => field.name );


