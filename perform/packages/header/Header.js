import  classnames      from 'classnames';
import { h }            from 'preact';

import PlanSelector     from '../plan-selector/PlanSelector';
import Status           from '../status/Status';
import { useStores }    from '../vesa/';

import S                from './Header.sass';


export default useStores([])(({ className, onClickLogo }) =>
    <div className={ classnames( S.className, className ) }>
        <div className={ S.left }>
            <img
                className={ S.logo }
                onClick={ onClickLogo }
                src="/accelerated-text-logo.png"
                title="Accelerated Text"
            />
        </div>
        <div className={ S.center }>
            <PlanSelector />
        </div>
        <div className={ S.right }>
            <Status />
        </div>
    </div>
);
