import { h }                from 'preact';

import { useStores }        from '../vesa/';

import FlagValue            from './FlagValue';
import S                    from './Configuration.sass';


export default useStores([
    'reader',
])(({
    E,
    reader: { flags, flagValues },
}) =>
    <div className={ S.className }>
        { flags.map( flag =>
            <FlagValue
                key={ flag.id }
                flag={ flag }
                flagValues={ flagValues }
                onChange={ E.reader.onToggleFlag }
            />
        )}
    </div>
);
