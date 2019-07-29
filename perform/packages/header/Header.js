import  classnames          from 'classnames';
import { h }                from 'preact';

import getOpenedPlan        from '../plan-list/get-opened-plan';
import PlanSelector         from '../plan-selector/PlanSelector';
import QuickSearchLauncer   from '../quick-search/Launcher';
import Status               from '../status/Status';
import { useStores }        from '../vesa/';

import S                    from './Header.sass';


export default useStores([
    'documentPlans',
    'planList',
])(({ className, openQuickSearch, ...props }) => {
    const openedPlan =  getOpenedPlan( props );

    return (
        <div className={ classnames( S.className, className ) }>
            <div className={ S.left }>
                <img
                    className={ S.logo }
                    src="/accelerated-text-logo.png"
                    title="Accelerated Text"
                />
            </div>
            <div className={ S.center }>
                <PlanSelector openedPlan={ openedPlan } />
            </div>
            <div className={ S.right }>
                <QuickSearchLauncer onClick={ openQuickSearch } />
                <Status className={ S.status } openedPlan={ openedPlan } />
            </div>
        </div>
    );
});
