import { h }                from 'preact';

import BottomBarProvider    from '../bottom-bar/ContextProvider';
import {
    composeQueries,
    GraphQLProvider,
}                           from '../graphql/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import DocumentPlansProvider    from '../document-plans/ContextProvider';
import EditorSidebar        from '../plan-editor/Sidebar';
import GlobalShortcuts      from '../shortcuts/GlobalShortcuts';
import Header               from '../header/Header';
import Modal                from '../modal/Modal';
import ModalProvider        from '../modal/ContextProvider';
import PlanEditor           from '../plan-editor/PlanEditor';
import ReaderContextProvider    from '../reader/ContextProvider';
import VariantsContextProvider  from '../variants/ContextProvider';
import WorkspaceContextProvider from '../workspace-context/Provider';

import {
    acceleratedText,
    closeDictionaryItem,
}   from './local-state';
import OpenedFileProvider   from './OpenedDataFileContextProvider';
import OpenedPlanProvider   from './OpenedPlanContextProvider';
import S                    from './AcceleratedText.sass';


const AcceleratedText = composeQueries({
    acceleratedText,
    closeDictionaryItem,
})(({
    acceleratedText: { acceleratedText },
    closeDictionaryItem,
}) =>
    <GlobalShortcuts className={ S.className }>
        <div className={ S.grid }>
            <Header
                className={ S.header }
                onClickLogo={ closeDictionaryItem }
            />
            {
                ( acceleratedText && acceleratedText.openedDictionaryItem )
                    ? <DictionaryEditor
                        className={ S.main }
                        closeEditor={ closeDictionaryItem }
                        itemId={ acceleratedText.openedDictionaryItem }
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
    DocumentPlansProvider,
    OpenedPlanProvider,
    OpenedFileProvider,
    ReaderContextProvider,
    VariantsContextProvider,
    WorkspaceContextProvider,
    AcceleratedText,
    null,
].reverse().reduce(
    ( children, Parent ) => h( Parent, { children })
);
