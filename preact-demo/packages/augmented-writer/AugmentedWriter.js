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
import planListLsAdapter    from '../plan-list/local-storage-adapter';
import { mount }            from '../vesa/';
import PlanEditor           from '../plan-editor/PlanEditor';
import user                 from '../user/store';
import variantsApi          from '../variants-api/store';
import variantsApiAdapter   from '../variants-api/adapter';

import S                from './AugmentedWriter.sass';


export default mount({
    contexts,
    dataSamples,
    documentPlans,
    planList,
    user,
    variantsApi,
}, [
    contextsAdapter,
    dataSamplesAdapter,
    documentPlansAdapter,
    planListAdapter,
    variantsApiAdapter,
    planListLsAdapter,
])(() =>
    <div className={ S.className }>
        <Header className={ S.header } />
        <PlanEditor className={ S.main } />
        <EditorSidebar className={ S.rightSidebar } />
    </div>
);
