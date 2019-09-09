import { h }                from 'preact';

import {
    composeQueries,
    GraphQLProvider,
}   from '../graphql/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import DocumentPlansContextProvider from '../document-plans/ContextProvider';
import EditorSidebar        from '../plan-editor/Sidebar';
import Header               from '../header/Header';
import PlanEditor           from '../plan-editor/PlanEditor';
import QuickSearchModal     from '../quick-search/WorkspaceModal';
import QuickSearchShortcuts from '../quick-search/KeyboardProvider';
import VariantsContextProvider  from '../variants/ContextProvider';
import WorkspaceContextProvider from '../workspace-context/Provider';

import {
    acceleratedText,
    closeDictionaryItem,
    closeQuickSearch,
    openQuickSearch,
}   from './local-state';
import mountStores          from './mount-stores';
import OpenedPlanProvider   from './OpenedPlanContextProvider';
import S                    from './AcceleratedText.sass';


const AcceleratedText = composeQueries({
    acceleratedText,
    closeDictionaryItem,
    closeQuickSearch,
    openQuickSearch,
})(({
    acceleratedText: { acceleratedText },
    closeDictionaryItem,
    closeQuickSearch,
    openQuickSearch,
}) =>
    <QuickSearchShortcuts
        className={ S.className }
        openQuickSearch={ openQuickSearch }
    >
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
        {
            ( acceleratedText && acceleratedText.openedQuickSearch )
                ? <QuickSearchModal onClose={ closeQuickSearch } />
                : null
        }
    </QuickSearchShortcuts>
);


export default mountStores(
    () =>
        <GraphQLProvider>
            <OpenedPlanProvider>
                <DocumentPlansContextProvider>
                    <VariantsContextProvider>
                        <WorkspaceContextProvider>
                            <AcceleratedText />
                        </WorkspaceContextProvider>
                    </VariantsContextProvider>
                </DocumentPlansContextProvider>
            </OpenedPlanProvider>
        </GraphQLProvider>
);
