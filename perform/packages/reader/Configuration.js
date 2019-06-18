import { h }                from 'preact';

import { useStores }        from '../vesa/';

import FlagValue            from './FlagValue';
import S                    from './Configuration.sass';


export default useStores([
    'reader',
])(({
    E,
    reader: { flagValues, readerFlags },
}) =>
    <div className={ S.className }>
        { readerFlags.flags.map( flag =>
            <FlagValue
                key={ flag.id }
                flag={ flag }
                flagValues={ flagValues }
                onChange={ E.reader.onToggleFlag }
            />
        )}
    </div>
);
