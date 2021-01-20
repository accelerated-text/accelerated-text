import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import composeContexts      from '../compose-contexts/';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OnboardCode          from '../onboard-code/RglOnboardCode';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import PlanActions          from '../document-plans/Actions';
import planTemplate         from '../document-plans/rgl-plan-template';
import Workspace            from '../nlg-workspace/NlgWorkspace';

import S                    from './PlanEditor.sass';


export default PlanActions( composeContexts({
    openedDataFile:         OpenedFileContext,
    openedPlan:             OpenedPlanContext,
})( class PlanEditor extends Component {

    static propTypes = {
        className:          PropTypes.string,
        onCreatePlan:       PropTypes.func.isRequired,
        onUpdatePlan:       PropTypes.func.isRequired,
        openedDataFile:     PropTypes.object.isRequired,
        openedPlan:         PropTypes.object.isRequired,
    };

    onChangeWorkspace = ({ documentPlan, workspaceXml }) => {
        this.props.onUpdatePlan({
            ...this.props.openedPlan.plan,
            documentPlan,
            blocklyXml:     workspaceXml,
        });
    };

    onCreateXml = blocklyXml => {
        this.props.onCreatePlan({
            name:           planTemplate.name,
            blocklyXml,
        });
    };

    render({
        className,
        openedDataFile: { file },
        openedPlan: { error, loading, plan },
    }) {
        return (
            <div className={ classnames( S.className, className ) }>{
                plan
                    ? <Workspace
                        cellNames={ file && file.fieldNames }
                        key={ plan.uid }
                        onChangeWorkspace={ this.onChangeWorkspace }
                        workspaceXml={ plan.blocklyXml }
                    />
                : loading
                    ? <Loading className={ S.item } message="Loading RGL." />
                : error
                    ? <Error className={ S.item } message="Error loading RGL." />
                    : <OnboardCode onCreateXml={ this.onCreateXml } />
            }</div>
        );
    }
}));
