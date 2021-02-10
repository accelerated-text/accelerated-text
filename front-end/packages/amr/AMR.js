import { h }                from 'preact';

import BottomBarProvider    from '../bottom-bar/ContextProvider';
import composeContexts      from '../compose-contexts/';
import { AMRContextProvider }    from '../document-plans/ContextProvider';
import EditorSidebar        from '../plan-editor/ParadigmsSidebar';
import GlobalShortcuts      from '../shortcuts/GlobalShortcuts';
import { GraphQLProvider }  from '../graphql/';
import Header               from '../header/AmrHeader';
import Modal                from '../modal/Modal';
import ModalProvider        from '../modal/ContextProvider';
import PlanEditor           from '../plan-editor/ParadigmsPlanEditor';
import WorkspaceContextProvider from '../workspace-context/Provider';
import OpenedPlanProvider   from './OpenedPlanContextProvider';
import S                    from './AMR.sass';
import UIContext            from './UIContext';
import UIContextProvider    from './UIContextProvider';


const AMR = composeContexts({
    uiContext:              UIContext,
})(({
    uiContext: {},
}) =>
    <GlobalShortcuts className={ S.className }>
        <div className={ S.grid }>
            <Header className={ S.header } />
            <PlanEditor className={ S.main } />
            <EditorSidebar className={ S.rightSidebar } />
        </div>
        <Modal />
    </GlobalShortcuts>
);


export default () => [
    ModalProvider,
    BottomBarProvider,
    GraphQLProvider,
    AMRContextProvider,
    OpenedPlanProvider,
    WorkspaceContextProvider,
    UIContextProvider,
    AMR,
    null,
].reverse().reduce(
    ( children, Parent ) => h( Parent, { children })
);
