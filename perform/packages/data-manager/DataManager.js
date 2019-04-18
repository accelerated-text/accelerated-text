import { h }                from 'preact';

import { findFileByPlan }   from '../data-samples/functions';
import { useStores }        from '../vesa/';

import Cells                from './Cells';
import Files                from './Files';
import S                    from './DataManager.sass';


export default useStores([
    'dataSamples',
])(({
    dataSamples,
    plan,
}) => {
    const planFile =        findFileByPlan( dataSamples, plan );

    return (
        <div className={ S.className }>
            <Files className={ S.files } plan={ plan } />
            { planFile && planFile.fieldNames &&
                <Cells className={ S.cells } planFile={ planFile } />
            }
        </div>
    );
});
