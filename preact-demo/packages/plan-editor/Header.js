import classnames       from 'classnames';
import { h }            from 'preact';

import { useStores }    from '../vesa/';

import S                from './Header.sass';
import SelectContext    from './SelectContext';
import UploadDataSample from './UploadDataSample';


export default useStores([
    'planEditor',
])(({
    className,
    planEditor: {
        planName,
    },
}) =>
    <div className={ classnames( S.className, className ) }>
        <select className={ S.name }>
            <optgroup label="Select a plan">
                <option selected>{ planName }</option>
            </optgroup>
            <option disabled>New...</option>
        </select>
        <span>Context: <SelectContext /></span>
        <span>Data sample: <UploadDataSample /></span>
    </div>
);
