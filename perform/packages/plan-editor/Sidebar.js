import { h }                from 'preact';

import AmrConcepts          from '../amr-concepts/AmrConcepts';
import DataManager          from '../data-manager/DataManager';
import Dictionary           from '../dictionary/Dictionary';
import FakeShop             from '../fake-shop/SidebarItem';
import { findFileByPlan }   from '../data-samples/functions';
import getOpenedPlan        from '../plan-list/get-opened-plan';
import ReaderConfiguration  from '../reader/Configuration';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';
import { useStores }        from '../vesa/';


export default useStores([
    'dataSamples',
    'documentPlans',
    'planList',
])(({ className, dataSamples, E, ...props }) => {

    const openedPlan =      getOpenedPlan( props );
    const fileItem =        findFileByPlan( dataSamples, openedPlan );

    return (
        <Sidebar className={ className }>
            <SidebarItem
                isExpanded={ !! fileItem }
                title={
                    fileItem
                        ? `Data (${ fileItem.fileName }: ${ openedPlan.dataSampleRow + 1 })`
                        : 'Data'
                }
            >
                <DataManager plan={ openedPlan } />
            </SidebarItem>
            <SidebarItem title="Reader">
                <ReaderConfiguration />
            </SidebarItem>
            <SidebarItem isExpanded title="Publish to the shop">
                <FakeShop
                    fileItem={ fileItem }
                    plan={ openedPlan }
                />
            </SidebarItem>
            <SidebarItem title="Text Analysis">
                <VariantReview />
            </SidebarItem>
            <SidebarItem title="AMR">
                <AmrConcepts />
            </SidebarItem>
            <SidebarItem title="Dictionary">
                <Dictionary />
            </SidebarItem>
        </Sidebar>
    );
});
