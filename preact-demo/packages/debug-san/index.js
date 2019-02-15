import debug            from 'debug';
import pTap             from 'p-tap';


export default modulePrefix => {

    const debugFn =     debug( modulePrefix );
    const error =       debug( `${ modulePrefix }:error` );
    const log =         debug( `${ modulePrefix }:log` );
    log.log =           console.log.bind( console ); // eslint-disable-line no-console

    debugFn.error =     error;
    debugFn.log =       log;

    debugFn.tapCatch = linePrefix =>
        pTap.catch( error.bind( null, linePrefix ));

    debugFn.tapThen = linePrefix =>
        pTap( log.bind( null, linePrefix ));

    return debugFn;
};
