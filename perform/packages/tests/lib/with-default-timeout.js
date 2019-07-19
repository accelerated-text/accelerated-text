export default ( t, run, ...args ) => {
    t.timeout( 8e3 );

    return run( t, ...args );
};
