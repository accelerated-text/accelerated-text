import { h }                from 'preact';

import { composeQueries }   from '../graphql/';
import {
    Error,
    Loading,
}   from '../ui-messages/';
import { readerFlags }      from '../graphql/queries.graphql';
import sortFlags            from '../reader-flags/sort';
import { useStores }        from '../vesa/';

import FlagValue            from './FlagValue';
import S                    from './Configuration.sass';


export default composeQueries({
    readerFlags,
})( useStores([
    'reader',
])(({
    E,
    reader: { flagValues },
    readerFlags: {
        error,
        loading,
        readerFlags,
    },
}) =>
    <div className={ S.className }>{
        error
            ? <Error message={ error } />
        : loading
            ? <Loading />
        : sortFlags( readerFlags ).map( flag =>
            <FlagValue
                key={ flag.id }
                flag={ flag }
                isChecked={ flagValues[flag.id] }
                onChange={ E.reader.onToggleFlag }
            />
        )
    }</div>
));
