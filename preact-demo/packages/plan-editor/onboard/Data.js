import { h }            from 'preact';

import OnboardBlocker   from '../../onboard-blocker/OnboardBlocker';
import SelectContext    from '../../plan-selector/SelectContext';
import { useStores }    from '../../vesa/';

import UploadDataSample from '../UploadDataSample';

import S                from './Data.sass';

export default useStores([
    'planEditor',
])(({
    children,
    planEditor: {
        contextName,
        dataSample,
    },
}) =>
    <div className={ S.className }>
        { !dataSample &&
            <div className={ S.task }>
                { 'Please ' }
                <UploadDataSample />
                { ' a data sample CSV.' }
            </div>
        }
        { !contextName &&
            <div className={ S.task }>
                { 'Please ' }
                <SelectContext />
            </div>
        }
        <OnboardBlocker showBlock={ !dataSample || !contextName }>
            { children }
        </OnboardBlocker>
    </div>
);
