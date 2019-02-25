import classnames       from 'classnames';
import { h }            from 'preact';

import SelectContext    from '../document-plans/SelectContext';
import SelectDataSample from '../document-plans/SelectDataSample';
import PlanSelector     from '../plan-selector/PlanSelector';

import S                from './Header.sass';


export default ({ className, openedPlan }) =>
    <div className={ classnames( S.className, className ) }>
        <PlanSelector openedPlan={ openedPlan } />
        <span>Context: <SelectContext plan={ openedPlan } /></span>
        <span>Data sample: <SelectDataSample plan={ openedPlan } /></span>
    </div>;
