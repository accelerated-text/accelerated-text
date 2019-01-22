import classnames           from 'classnames';
import { h }                from 'preact';

import DocumentPlanSelector from '../document-plan-selector/DocumentPlanSelector';
import { useStores }        from '../vesa/';

import S                    from './Header.sass';
import SelectContext        from './SelectContext';
import UploadDataSample     from './UploadDataSample';


export default useStores([
    'planEditor',
])(({
    className,
    planEditor: {
        planName,
    },
}) =>
    <div className={ classnames( S.className, className ) }>
        <DocumentPlanSelector planName={ planName } />
        <span>Context: <SelectContext /></span>
        <span>Data sample: <UploadDataSample /></span>
    </div>
);
