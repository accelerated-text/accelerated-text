import { h }                from 'preact';

import contexts             from '../contexts/store';
import contextsAdapter      from '../contexts/adapter';
import documentPlans        from '../document-plans/store';
import documentPlansAdapter from '../document-plans/adapter';
import planList             from '../plan-list/store';
import planListAdapter      from '../plan-list/adapter';
import { mount }            from '../vesa/';
import PlanEditor           from '../plan-editor/PlanEditor';
import planEditor           from '../plan-editor/store';
import VariantReview        from '../variant-review/VariantReview';
import variantsApi          from '../variants-api/store';
import variantsApiAdapter   from '../variants-api/adapter';

import S                from './AugmentedWriter.sass';


export default mount({
    contexts,
    documentPlans,
    planEditor,
    planList,
    variantsApi,
}, [
    contextsAdapter,
    documentPlansAdapter,
    planListAdapter,
    variantsApiAdapter,
])(() =>
    <div className={ S.className }>
        <PlanEditor />
        <VariantReview />
    </div>
);
