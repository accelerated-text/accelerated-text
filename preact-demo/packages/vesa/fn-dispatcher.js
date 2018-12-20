import { Dispatcher }   from 'flux';

export default class FnDispatcher {

    dispatcher =        new Dispatcher;
    fnMap =             new Map;

    register = fn =>
        this.fnMap.set( fn, this.dispatcher.register( fn ));

    unregister = fn => {
        const token =   this.fnMap.get( fn );
        this.dispatcher.unregister( token );
        this.fnMap.delete( fn );
    };

    dispatch = payload =>
        this.dispatcher.dispatch( payload );
}

