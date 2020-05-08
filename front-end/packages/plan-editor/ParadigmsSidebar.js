import { h }                from 'preact';

import ParadigmsConcepts    from '../amr-concepts/ParadigmsConcepts';
import ParadigmsStructural  from '../amr-concepts/ParadigmsStructural';
import ParadigmsGrammar     from '../amr-concepts/ParadigmsGrammar';
import ParadigmsEngConcepts from '../amr-concepts/ParadigmsEngConcepts';
import ParadigmsEstConcepts from '../amr-concepts/ParadigmsEstConcepts';
import ParadigmsGerConcepts from '../amr-concepts/ParadigmsGerConcepts';
import ParadigmsLavConcepts from '../amr-concepts/ParadigmsLavConcepts';
import ParadigmsRusConcepts from '../amr-concepts/ParadigmsRusConcepts';
import ParadigmsSpaConcepts from '../amr-concepts/ParadigmsSpaConcepts';
import composeContexts      from '../compose-contexts/';
import Dictionary           from '../dictionary/Dictionary';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';


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
        <SidebarItem title="English">
            <ParadigmsEngConcepts />
        </SidebarItem>
        <SidebarItem title="Estonian">
            <ParadigmsEstConcepts />
        </SidebarItem>
        <SidebarItem title="German">
            <ParadigmsGerConcepts />
        </SidebarItem>
        <SidebarItem title="Latvian">
            <ParadigmsLavConcepts />
        </SidebarItem>
        <SidebarItem title="Russian">
            <ParadigmsRusConcepts />
        </SidebarItem>
        <SidebarItem title="Spanish">
            <ParadigmsSpaConcepts />
        </SidebarItem>
        <SidebarItem title="Operations">
            <ParadigmsConcepts />
        </SidebarItem>
        <SidebarItem title="Structural Words">
            <ParadigmsStructural />
        </SidebarItem>
        <SidebarItem title="Grammar">
            <ParadigmsGrammar />
        </SidebarItem>
        <SidebarItem title="Dictionary">
            <Dictionary />
        </SidebarItem>
    </Sidebar>
);
