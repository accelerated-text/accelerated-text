import { h }                from 'preact';

import SelectDataSample     from '../document-plans/SelectDataSample';
import UploadDataFile       from '../upload-data-file/UploadDataFile';
import { useStores }        from '../vesa/';

import getPlanFile          from './get-plan-file';
import S                    from './DataManager.sass';


export default useStores([
    'dataSamples',
])(({
    dataSamples: {
        files,
        getListError,
        getListLoading,
    },
    plan,
}) => {
    const planFile =        getPlanFile( files, plan );

    return (
        <div className={ S.className }>
            <div className={ S.files }>
                <SelectDataSample plan={ plan } />
                <UploadDataFile />
            </div>
            { planFile && planFile.fieldNames &&
                <ul>{ planFile.fieldNames.map( name =>
                    <li>{ name }</li>
                )}</ul>
            }
        </div>
    );
});
