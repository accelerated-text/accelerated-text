import { h }            from 'preact';

import OnboardBlocker   from '../onboard-blocker/OnboardBlocker';
import useStores        from '../context/use-stores';

import SelectContext    from './SelectContext';
import S                from './OnboardData.sass';
import UploadDataSample from './UploadDataSample';


export default useStores([
    'generatorEditor',
])(
    ({
        children,
        generatorEditor: {
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
