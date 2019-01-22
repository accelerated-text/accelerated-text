import classnames       from 'classnames';
import { h }            from 'preact';

import PlanSelector     from '../plan-selector/PlanSelector';

import S                from './Header.sass';
import SelectContext    from './SelectContext';
import UploadDataSample from './UploadDataSample';


export default ({ className }) =>
    <div className={ classnames( S.className, className ) }>
        <PlanSelector />
        <span>Context: <SelectContext /></span>
        <span>Data sample: <UploadDataSample /></span>
    </div>;
