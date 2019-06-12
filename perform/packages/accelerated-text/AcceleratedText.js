import { h }                from 'preact';

import {
    composeQueries,
    GraphQLProvider,
}   from '../graphql/';
import DictionaryEditor     from '../dictionary-editor/DictionaryEditor';
import EditorSidebar        from '../plan-editor/Sidebar';
import Header               from '../header/Header';
import PlanEditor           from '../plan-editor/PlanEditor';

import { acceleratedText }  from './local-state';
import mountStores          from './mount-stores';
import S                    from './AcceleratedText.sass';


const AcceleratedText = mountStores(
    composeQueries({
        acceleratedText,
    })(({
        acceleratedText: { acceleratedText },
    }) =>
        <div className={ S.className }>
            <Header className={ S.header } />
            {
                ( acceleratedText && acceleratedText.openedDictionaryItem )
                    ? <DictionaryEditor
                        className={ S.main }
                        openedPhrase={ acceleratedText.openedDictionaryItem }
                    />
                    : <PlanEditor className={ S.main } />
            }
            <EditorSidebar className={ S.rightSidebar } />
        </div>
    )
);


export default () =>
    <GraphQLProvider>
        <AcceleratedText />
    </GraphQLProvider>;
