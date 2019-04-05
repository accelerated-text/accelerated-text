import  classnames      from 'classnames';
import { h }            from 'preact';

import getOpenedPlan    from '../plan-list/get-opened-plan';
import PlanSelector     from '../plan-selector/PlanSelector';
import Status           from '../status/Status';
import { useStores }    from '../vesa/';

import S                from './Header.sass';


export default useStores([
    'documentPlans',
    'planList',
])(({ className, ...props }) => {
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
                <Status openedPlan={ openedPlan } />
            </div>
        </div>
    );
});
