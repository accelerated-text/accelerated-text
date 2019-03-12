import { h }                from 'preact';

import SelectDataSample     from '../document-plans/SelectDataSample';
import { useStores }        from '../vesa/';

import getPlanFile          from './get-plan-file';


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
        <div>
            <SelectDataSample plan={ plan } />
            { planFile && planFile.fieldNames &&
                <ul>{ planFile.fieldNames.map( name =>
                    <li>{ name }</li>
                )}</ul>
            }
        </div>
    );
});
