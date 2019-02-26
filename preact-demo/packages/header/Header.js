import  classnames      from 'classnames';
import { h }            from 'preact';

import getOpenedPlan    from '../plan-list/get-opened-plan';
import SelectContext    from '../document-plans/SelectContext';
import SelectDataSample from '../document-plans/SelectDataSample';
import PlanSelector     from '../plan-selector/PlanSelector';
import { useStores }    from '../vesa/';

import S                from './Header.sass';


export default useStores([
    'documentPlans',
    'planList',
])(({ className, ...props }) => {
    const openedPlan =  getOpenedPlan( props );

    return (
        <div className={ classnames( S.className, className ) }>
            <img
                className={ S.logo }
                src="/augmented-writer-logo.png"
                title="Augmented Writer"
            />
            <PlanSelector openedPlan={ openedPlan } />
            <span>Context: <SelectContext plan={ openedPlan } /></span>
            <span>Data sample: <SelectDataSample plan={ openedPlan } /></span>
        </div>
    );
});
