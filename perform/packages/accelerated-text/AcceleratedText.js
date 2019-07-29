import { h, Component }     from 'preact';

import {
    composeQueries,
    GraphQLProvider,
}   from '../graphql/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import EditorSidebar        from '../plan-editor/Sidebar';
import Header               from '../header/Header';
import PlanEditor           from '../plan-editor/PlanEditor';
import QuickSearch          from '../quick-search/Modal';

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
    })( class AcceleratedText extends Component {

        onKeyDown = evt => {
            if( evt.ctrlKey && evt.key === '/' ) {
                this.props.openQuickSearch();
            }
        }

        render({
            acceleratedText: { acceleratedText },
            closeDictionaryItem,
            closeQuickSearch,
            openQuickSearch,
        }) {
            return (
                <div className={ S.className } onKeyDown={ this.onKeyDown } tabindex="0">
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
                            ? <QuickSearch onClose={ closeQuickSearch } />
                            : null
                    }
                </div>
            );
        }
    })
);


export default () =>
    <GraphQLProvider>
        <AcceleratedText />
    </GraphQLProvider>;
