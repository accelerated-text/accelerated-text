export default ( t, run, ...args ) => {
    t.timeout( 32e3 );

    return run( t, ...args );
};
