import mountStore       from './mount-store';


export default ( storeMap = {}, adapters = []) => Child =>
    Object
        .getOwnPropertyNames( storeMap )
        .reverse()
        .reduce(
            ( childComponent, name ) =>
                mountStore( name, storeMap[name])( childComponent ),
            adapters.reduce(
                ( childComponent, adapter ) =>
                    mountStore( null, adapter )( childComponent ),
                Child,
            ),
        );
