import { h }            from 'preact';

import PlanEditor       from '../plan-editor/PlanEditor';
import planEditorStore  from '../plan-editor/store';
import provideStore     from '../context/provide-store';
import VariantReview    from '../variant-review/VariantReview';

import S                from './AugmentedWriter.sass';


export default provideStore(
    'planEditor', planEditorStore,
)(() =>
    <div className={ S.className }>
        <PlanEditor />
        <VariantReview />
    </div>
);
