import debug            from 'debug';
import pTap             from 'p-tap';


export default modulePrefix => {

    const debugFn =     debug( modulePrefix );
    const error =       debugFn.extend( 'error' );
    const info =        debugFn.extend( 'info' );
    info.log =          console.log.bind( console ); // eslint-disable-line no-console

    debugFn.error =     error;
    debugFn.info =      info;

    debugFn.tapCatch = ( linePrefix = '' ) =>
        pTap.catch( debugFn.bind( null, linePrefix, 'catch' ));

    debugFn.tapThen = ( linePrefix = '' ) =>
        pTap( debugFn.bind( null, linePrefix, 'then' ));

    return debugFn;
};
