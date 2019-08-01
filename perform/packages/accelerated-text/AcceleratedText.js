import { h }                from 'preact';

import {
    composeQueries,
    GraphQLProvider,
}   from '../graphql/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import EditorSidebar        from '../plan-editor/Sidebar';
import Header               from '../header/Header';
import PlanEditor           from '../plan-editor/PlanEditor';
import QuickSearchModal     from '../quick-search/WorkspaceModal';
import QuickSearchShortcuts from '../quick-search/KeyboardProvider';
import WorkspaceContextProvider from '../workspace-context/Provider';

import {
    acceleratedText,
    closeDictionaryItem,
    closeQuickSearch,
    openQuickSearch,
}   from './local-state';
import mountStores          from './mount-stores';
import S                    from './AcceleratedText.sass';


const AcceleratedText = mountStores(
    composeQueries({
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
                    openQuickSearch={ openQuickSearch }
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
    )
);


export default () =>
    <GraphQLProvider>
        <WorkspaceContextProvider>
            <AcceleratedText />
        </WorkspaceContextProvider>
    </GraphQLProvider>;
