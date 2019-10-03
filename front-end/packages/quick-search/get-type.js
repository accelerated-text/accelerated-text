export default ({ types }, n = 0 ) =>
    types[
        ( types.length + n % types.length ) % types.length
    ];
