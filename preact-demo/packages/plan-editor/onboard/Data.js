import { h }            from 'preact';

import ContextsSelect   from '../../contexts/Select';
import OnboardBlocker   from '../../onboard-blocker/OnboardBlocker';
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
                <ContextsSelect />
            </div>
        }
        <OnboardBlocker showBlock={ !dataSample || !contextName }>
            { children }
        </OnboardBlocker>
    </div>
);
