import { h }                from 'preact';

import RglConcepts          from '../amr-concepts/RglConcepts';
import composeContexts      from '../compose-contexts/';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import Sidebar              from '../sidebar/Sidebar';


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
        <RglConcepts />
    </Sidebar>
);
