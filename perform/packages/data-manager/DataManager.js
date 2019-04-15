import { h }                from 'preact';

import { useStores }        from '../vesa/';

import Cells                from './Cells';
import Files                from './Files';
import getPlanFile          from './get-plan-file';
import S                    from './DataManager.sass';


export default useStores([
    'dataSamples',
])(({
    dataSamples: { files },
    plan,
}) => {
    const planFile =        getPlanFile( files, plan );

    return (
        <div className={ S.className }>
            <Files plan={ plan } />
            { planFile && planFile.fieldNames &&
                <Cells planFile={ planFile } />
            }
        </div>
    );
});
