import { h }                from 'preact';

import BottomBarProvider    from '../bottom-bar/ContextProvider';
import composeContexts      from '../compose-contexts/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import { DPContextProvider }    from '../document-plans/ContextProvider';
import EditorSidebar        from '../plan-editor/Sidebar';
import GlobalShortcuts      from '../shortcuts/GlobalShortcuts';
import { GraphQLProvider }  from '../graphql/';
import Header               from '../header/Header';
import Modal                from '../modal/Modal';
import ModalProvider        from '../modal/ContextProvider';
import PlanEditor           from '../plan-editor/PlanEditor';
import ReaderContextProvider    from '../reader/ContextProvider';
import VariantsContextProvider  from '../variants/ContextProvider';
import WorkspaceContextProvider from '../workspace-context/Provider';

import OpenedFileProvider   from './OpenedDataFileContextProvider';
import OpenedPlanProvider   from './OpenedPlanContextProvider';
import S                    from './AcceleratedText.sass';
import UIContext            from './UIContext';
import UIContextProvider    from './UIContextProvider';


const AcceleratedText = composeContexts({
    uiContext:              UIContext,
})(({
    uiContext: {
        closeDictionaryItem,
        dictionaryItemId,
    },
}) =>
    <GlobalShortcuts className={ S.className }>
        <div className={ S.grid }>
            <Header className={ S.header } />
            { dictionaryItemId
                ? <DictionaryEditor
                    className={ S.main }
                    closeEditor={ closeDictionaryItem }
                    itemId={ dictionaryItemId }
                />
                : <PlanEditor className={ S.main } />
            }
            <EditorSidebar className={ S.rightSidebar } />
        </div>
        <Modal />
    </GlobalShortcuts>
);


export default () => [
    ModalProvider,
    BottomBarProvider,
    GraphQLProvider,
    DPContextProvider,
    OpenedPlanProvider,
    OpenedFileProvider,
    ReaderContextProvider,
    VariantsContextProvider,
    WorkspaceContextProvider,
    UIContextProvider,
    AcceleratedText,
    null,
].reverse().reduce(
    ( children, Parent ) => h( Parent, { children })
);
