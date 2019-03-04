import { h }                from 'preact';

import getOpenedPlan        from '../plan-list/get-opened-plan';
import Lexicon              from '../lexicon/Lexicon';
import SelectContext        from '../document-plans/SelectContext';
import SelectDataSample     from '../document-plans/SelectDataSample';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';
import { useStores }        from '../vesa/';


export default useStores([
    'documentPlans',
    'planList',
])(({ className, ...props }) => {

    const openedPlan =      getOpenedPlan( props );

    return (
        <Sidebar className={ className }>
            <SidebarItem isExpanded title="Preview">
                <VariantReview />
            </SidebarItem>
            <SidebarItem isExpanded title="Data">
                <SelectDataSample plan={ openedPlan } />
            </SidebarItem>
            <SidebarItem isExpanded title="Context">
                Topic: <SelectContext plan={ openedPlan } />
            </SidebarItem>
            <SidebarItem isExpanded title="Word lists">
                <Lexicon />
            </SidebarItem>
        </Sidebar>
    );
});
