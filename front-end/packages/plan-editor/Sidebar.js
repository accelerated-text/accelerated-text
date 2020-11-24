import { h }                 from 'preact';

import AmrConcepts           from '../amr-concepts/AmrConcepts';
import composeContexts       from '../compose-contexts/';
import DataManager           from '../data-manager/DataManager';
import Dictionary            from '../dictionary/Dictionary';
import OpenedFileContext     from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext     from '../accelerated-text/OpenedPlanContext';
import ReaderConfiguration   from '../reader/Configuration';
import LanguageConfiguration from '../reader/LanguageConfiguration';
import Sidebar               from '../sidebar/Sidebar';
import SidebarItem           from '../sidebar/Item';
import VariantReview         from '../variant-review/VariantReview';


export default composeContexts({
    openedDataFile:         OpenedFileContext,
    openedPlan:             OpenedPlanContext,
})(({
    className,
    openedDataFile: { file },
    openedPlan: {
        plan,
        loading,
    },
}) =>
    <Sidebar className={ className }>
        <SidebarItem
            isExpanded={ !! file }
            title={
                file
                    ? `Data (${ file.fileName }: ${ plan.dataSampleRow + 1 })`
                    : 'Data'
            }
        >
            <DataManager plan={ plan } />
        </SidebarItem>
        <SidebarItem title="Language">
            <LanguageConfiguration />
        </SidebarItem>
        <SidebarItem title="Reader Model">
            <ReaderConfiguration />
        </SidebarItem>
        <SidebarItem title="Text Generation Samples">
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
