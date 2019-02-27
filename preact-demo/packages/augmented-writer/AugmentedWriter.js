import { h }                from 'preact';

import contexts             from '../contexts/store';
import contextsAdapter      from '../contexts/adapter';
import dataSamples          from '../data-samples/store';
import dataSamplesAdapter   from '../data-samples/adapter';
import documentPlans        from '../document-plans/store';
import documentPlansAdapter from '../document-plans/adapter';
import EditorSidebar        from '../plan-editor/Sidebar';
import Header               from '../header/Header';
import planList             from '../plan-list/store';
import planListAdapter      from '../plan-list/adapter';
import { mount }            from '../vesa/';
import PlanEditor           from '../plan-editor/PlanEditor';
import Sidebar              from '../sidebar/Sidebar';
import variantsApi          from '../variants-api/store';
import variantsApiAdapter   from '../variants-api/adapter';

import S                from './AugmentedWriter.sass';


export default mount({
    contexts,
    dataSamples,
    documentPlans,
    planList,
    variantsApi,
}, [
    contextsAdapter,
    dataSamplesAdapter,
    documentPlansAdapter,
    planListAdapter,
    variantsApiAdapter,
])(() =>
    <div className={ S.className }>
        <Header className={ S.header } />
        <PlanEditor className={ S.main } />
        <Sidebar className={ S.rightSidebar }>
            <EditorSidebar />
        </Sidebar>
    </div>
);
