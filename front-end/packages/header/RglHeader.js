import  classnames          from 'classnames';
import { h }                from 'preact';

import composeContexts      from '../compose-contexts/';
import PlanSelector         from '../plan-selector/PlanSelector';
import Status               from '../status/Status';
import UIContext            from '../accelerated-text/UIContext';

import S                    from './Header.sass';


export default composeContexts({
    uiContext:              UIContext,
})(({
    className,
    uiContext: { closeAll, openHelp },
}) =>
    <div className={ classnames( S.className, className ) }>
        <div className={ S.left }>
            <img
                className={ S.logo }
                onClick={ closeAll }
                src="/accelerated-text-logo.png"
                title="DLG Editor"
            />
        </div>
        <div className={ S.center }>
            <PlanSelector />
        </div>
        <div className={ S.right }>
            <button
                className={ S.help }
                children="❔Help"
                onClick={ openHelp }
            />
            <Status className={ S.status } />
        </div>
    </div>
);
