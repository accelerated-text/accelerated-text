export default ( t, run, ...args ) => {
    t.timeout( 16e3 );

    return run( t, ...args );
};
