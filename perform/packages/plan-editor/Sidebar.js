import { h, Component }     from 'preact';

import AmrConcepts          from '../amr-concepts/AmrConcepts';
import DataManager          from '../data-manager/DataManager';
import Dictionary           from '../dictionary/Dictionary';
import DocumentPlansContext from '../document-plans/Context';
import FakeShop             from '../fake-shop/SidebarItem';
import ReaderConfiguration  from '../reader/Configuration';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';


export default class PlanEditorSidebar extends Component {

    static contextType =    DocumentPlansContext;

    render({ className }, _, { openedPlan, openedPlanLoading, openedDataFile }) {
        return (
            <Sidebar className={ className }>
                <SidebarItem
                    isExpanded={ !! openedDataFile }
                    title={
                        openedDataFile
                            ? `Data (${ openedDataFile.fileName }: ${ openedPlan.dataSampleRow + 1 })`
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
                        dataFile={ openedDataFile }
                        plan={ openedPlan }
                        planLoading={ openedPlanLoading }
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
    }
}
