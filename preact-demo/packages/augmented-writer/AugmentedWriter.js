import { h }                from 'preact';

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
    planEditor,
    planList,
    variantsApi,
}, [
    planListAdapter,
    variantsApiAdapter,
])(() =>
    <div className={ S.className }>
        <PlanEditor />
        <VariantReview />
    </div>
);
