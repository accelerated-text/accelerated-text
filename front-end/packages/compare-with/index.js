/*
 * Give it a list of functions working on an array item
 * and it will return a function which can sort arrays.
 */
/// [( a → b)] → ( a, c ) → Number
export default ( fns = []) => ( a, b ) =>
    fns.filter( Boolean )
        .reduce(( acc, fn ) => (
            acc || fn( b ) - fn( a )
        ), 0 );
