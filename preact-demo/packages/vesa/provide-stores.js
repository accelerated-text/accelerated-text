import provideStore     from './provide-store';


export default ( storeMap = {}, adapters = []) => Child =>
    Object
        .getOwnPropertyNames( storeMap )
        .reverse()
        .reduce(
            ( childComponent, name ) =>
                provideStore( name, storeMap[name])( childComponent ),
            adapters.reduce(
                ( childComponent, adapter ) =>
                    provideStore( null, adapter )( childComponent ),
                Child,
            ),
        );
