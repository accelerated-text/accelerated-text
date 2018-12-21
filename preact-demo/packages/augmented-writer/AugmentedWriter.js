import { h }                from 'preact';

import PlanEditor           from '../plan-editor/PlanEditor';
import planEditor           from '../plan-editor/store';
import provideStores        from '../vesa/provide-stores';
import tokenizer            from '../tokenizer/store';
import tokenizerAdapter     from '../tokenizer/adapter';
import VariantReview        from '../variant-review/VariantReview';
import variantsApi          from '../variants-api/store';
import variantsApiAdapter   from '../variants-api/adapter';

import S                from './AugmentedWriter.sass';


export default provideStores({
    planEditor,
    tokenizer,
    variantsApi,
}, [
    tokenizerAdapter,
    variantsApiAdapter,
])(() =>
    <div className={ S.className }>
        <PlanEditor />
        <VariantReview />
    </div>
);
