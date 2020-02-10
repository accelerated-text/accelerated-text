import { h }                from 'preact';

import ParadigmsConcepts    from '../amr-concepts/ParadigmsConcepts';
import ParadigmsEngConcepts from '../amr-concepts/ParadigmsEngConcepts';
import ParadigmsGerConcepts from '../amr-concepts/ParadigmsGerConcepts';
import composeContexts      from '../compose-contexts/';
import DataManager          from '../data-manager/DataManager';
import Dictionary           from '../dictionary/Dictionary';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import ReaderConfiguration  from '../reader/Configuration';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';


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
        <SidebarItem title="RGL">
            <ParadigmsConcepts />
        </SidebarItem>
        <SidebarItem title="English Morphology">
            <ParadigmsEngConcepts />
        </SidebarItem>
        <SidebarItem title="German Morphology">
            <ParadigmsGerConcepts />
        </SidebarItem>
        <SidebarItem title="Dictionary">
            <Dictionary />
        </SidebarItem>
    </Sidebar>
);
