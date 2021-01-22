import { h }                from 'preact';

import BottomBarProvider    from '../bottom-bar/ContextProvider';
import composeContexts      from '../compose-contexts/';
import { RGLContextProvider }    from '../document-plans/ContextProvider';
import EditorSidebar        from '../plan-editor/RglSidebar';
import GlobalShortcuts      from '../shortcuts/GlobalShortcuts';
import { GraphQLProvider }  from '../graphql/';
import Header               from '../header/RglHeader';
import Modal                from '../modal/Modal';
import ModalProvider        from '../modal/ContextProvider';
import PlanEditor           from '../plan-editor/RglPlanEditor';
import VariantsContextProvider  from '../variants/AmrContextProvider';
import WorkspaceContextProvider from '../workspace-context/Provider';
import OpenedPlanProvider   from './OpenedPlanContextProvider';
import S                    from './RGL.sass';
import UIContext            from './UIContext';
import UIContextProvider    from './UIContextProvider';


const RGL = composeContexts({
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
    RGLContextProvider,
    OpenedPlanProvider,
    VariantsContextProvider,
    WorkspaceContextProvider,
    UIContextProvider,
    RGL,
    null,
].reverse().reduce(
    ( children, Parent ) => h( Parent, { children })
);
