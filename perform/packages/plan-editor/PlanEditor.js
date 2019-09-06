import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OnboardCode          from '../onboard-code/OnboardCode';
import PlanActions          from '../document-plans/Actions';
import planTemplate         from '../document-plans/plan-template';
import Workspace            from '../nlg-workspace/NlgWorkspace';

import S                    from './PlanEditor.sass';


export default PlanActions(
    class PlanEditor extends Component {

        static contextType =    DocumentPlansContext;

        static propTypes = {
            className:          PropTypes.string,
            onCreatePlan:       PropTypes.func.isRequired,
            onUpdatePlan:       PropTypes.func.isRequired,
        };

        onChangeWorkspace = ({ documentPlan, workspaceXml }) => {
            this.props.onUpdatePlan({
                ...this.context.openedPlan,
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
        }, _, {
            openedDataFile,
            openedPlan,
            openedPlanError,
            openedPlanLoading,
        }) {
            return (
                <div className={ classnames( S.className, className ) }>{
                    openedPlan
                        ? <Workspace
                            cellNames={ openedDataFile && openedDataFile.fieldNames }
                            key={ openedPlan.uid }
                            onChangeWorkspace={ this.onChangeWorkspace }
                            workspaceXml={ openedPlan.blocklyXml }
                        />
                    : openedPlanLoading
                        ? <Loading className={ S.item } message="Loading document plans." />
                    : openedPlanError
                        ? <Error className={ S.item } message="Error loading document plans." />
                        : <OnboardCode onCreateXml={ this.onCreateXml } />
                }</div>
            );
        }
    }
);
