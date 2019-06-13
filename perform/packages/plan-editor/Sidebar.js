import { h }                from 'preact';

import CcgOption            from '../ccg-option/CcgOption';
import DataManager          from '../data-manager/DataManager';
import Dictionary           from '../dictionary/Dictionary';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import ReaderConfiguration  from '../reader/Configuration';
import SelectContext        from '../document-plans/SelectContext';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';
import { useStores }        from '../vesa/';


export default useStores([
    'documentPlans',
    'planList',
])(({ className, E, ...props }) => {

    const openedPlan =      getOpenedPlan( props );

    return (
        <Sidebar className={ className }>
            <SidebarItem isExpanded title="Preview">
                <VariantReview />
            </SidebarItem>
            <SidebarItem title="Reader">
                <ReaderConfiguration />
            </SidebarItem>
            <SidebarItem isExpanded title="Data">
                <DataManager plan={ openedPlan } />
            </SidebarItem>
            <SidebarItem isExpanded title="Dictionary">
                <Dictionary />
            </SidebarItem>
            <SidebarItem title="Options">
                <div>
                    Topic: <SelectContext plan={ openedPlan } />
                </div>
                <CcgOption plan={ openedPlan } onChange={ E.documentPlans.onUpdate } />
            </SidebarItem>
        </Sidebar>
    );
});
