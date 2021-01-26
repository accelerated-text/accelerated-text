import { h }                from 'preact';

import RglConcepts          from '../amr-concepts/RglConcepts';
import composeContexts      from '../compose-contexts/';
import OpenedPlanContext    from '../rgl/OpenedPlanContext';
import Sidebar              from '../sidebar/Sidebar';


export default composeContexts({
    openedPlan:             OpenedPlanContext,
})(({
    className,
    openedPlan: {
        plan,
        loading,
    },
}) =>
    <Sidebar className={ className }>
        <RglConcepts />
    </Sidebar>
);
