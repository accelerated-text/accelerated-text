import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';
import { useStores }        from '../vesa/';

import Files                from './Files';
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
            <Files plan={ plan } />
            { planFile && planFile.fieldNames &&
                <div className={ S.fieldNames }>
                    { planFile.fieldNames.map( name =>
                        <DragInBlock
                            fields={{ name }}
                            text={ `${ name } cell` }
                            type="Cell"
                        />
                    )}
                </div>
            }
        </div>
    );
});
