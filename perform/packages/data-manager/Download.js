import { h }                from 'preact';

import {
    getDownloadUrl,
    findFileById,
}   from '../data-samples/functions';
import { useStores }        from '../vesa/';


export default useStores([
    'dataSamples',
    'user',
])(({
    className,
    dataSamples,
    plan,
    user,
}) => {
    const file =            findFileById( dataSamples, plan.dataSampleId );
    if( !file ) {
        return null;
    } else {
        return (
            <a
                children="Download file"
                className={ className }
                href={ getDownloadUrl( user, file ) }
            />
        );
    }
});
