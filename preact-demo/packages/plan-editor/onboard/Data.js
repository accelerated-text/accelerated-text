import { h }            from 'preact';

import OnboardBlocker   from '../../onboard-blocker/OnboardBlocker';
import { useStores }    from '../../vesa/';

import UploadDataSample from '../UploadDataSample';

import S                from './Data.sass';

export default useStores([
    'planEditor',
])(({
    children,
    planEditor: {
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
        <OnboardBlocker showBlock={ !dataSample }>
            { children }
        </OnboardBlocker>
    </div>
);
