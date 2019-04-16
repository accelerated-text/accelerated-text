import { h }                from 'preact';

import getFile              from '../data-samples/get-file';
import { useStores }        from '../vesa/';


export default useStores([
    'dataSamples',
    'user',
])(({
    className,
    dataSamples: { files },
    plan,
    user,
}) => {
    const file =            getFile({ files, id: plan.dataSampleId });
    if( !file ) {
        return null;
    } else {
        return (
            <a
                children="Download file"
                className={ className }
                href={ `${ process.env.DATA_FILES_BUCKET }/${ user.id }/${ file.fileName }` }
            />
        );
    }
});
