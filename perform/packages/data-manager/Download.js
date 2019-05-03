import { h }                from 'preact';

import getFileUrl           from '../upload-data-file/get-file-url';
import { findFileByPlan }   from '../data-samples/functions';
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
    const file =            findFileByPlan( dataSamples, plan );
    if( !file ) {
        return null;
    } else {
        return (
            <a
                children="Download file"
                className={ className }
                href={ getFileUrl( user, file.fileName ) }
            />
        );
    }
});
