import { h }                from 'preact';

import DataManager          from '../data-manager/DataManager';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import Lexicon              from '../lexicon/Lexicon';
import SelectContext        from '../document-plans/SelectContext';
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
                <DataManager plan={ openedPlan } />
            </SidebarItem>
            <SidebarItem isExpanded title="Word lists">
                <Lexicon />
            </SidebarItem>
            <SidebarItem isExpanded title="Context">
                Topic: <SelectContext plan={ openedPlan } />
            </SidebarItem>
        </Sidebar>
    );
});
