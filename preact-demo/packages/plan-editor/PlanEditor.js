import classnames       from 'classnames';
import { h }            from 'preact';

import OnboardCode      from '../onboard-code/OnboardCode';
import { useStores }    from '../vesa/';
import Workspace        from '../nlg-workspace/NlgWorkspace';

import Header           from './Header';
import OnboardData      from './onboard/Data';
import { QA }           from './qa.constants';
import S                from './PlanEditor.sass';


export default useStores([
    'planEditor',
    'planList',
])(({
    E,
    planEditor: {
        workspaceXml,
    },
    planList: {
        openedPlanUid,
    },
}) =>
    <div className={ S.className }>
        <Header className={ QA.HEADER } />
        <div className={ classnames( S.body, QA.BODY ) }>
            <OnboardData>
                <OnboardCode
                    blocklyXml={ workspaceXml }
                    onCreateXml={ E.planEditor.onCreateWorkspaceXml }
                />
            </OnboardData>
            { workspaceXml &&
                <Workspace
                    key={ openedPlanUid }
                    onChangeWorkspace={ E.planEditor.onChangeWorkspace }
                    workspaceXml={ workspaceXml }
                />
            }
        </div>
    </div>
);
