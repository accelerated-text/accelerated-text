import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { documentPlans }    from '../graphql/queries.graphql';
import DocumentPlansContext from '../document-plans/Context';
import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OnboardCode          from '../onboard-code/OnboardCode';
import planTemplate         from '../document-plans/plan-template';
import Workspace            from '../nlg-workspace/NlgWorkspace';

import S                    from './PlanEditor.sass';


export default composeQueries({
    documentPlans,
})( class PlanEditor extends Component {

    static contextType =    DocumentPlansContext;

    static propTypes = {
        className:          PropTypes.string,
    };

    onChangeWorkspace = ({ documentPlan, workspaceXml }) =>
        this.context.E.documentPlans.onUpdate({
            ...this.context.openedPlan,
            documentPlan,
            blocklyXml:     workspaceXml,
        });

    onCreateXml = blocklyXml =>
        this.context.E.documentPlans.onCreate.async({
            name:           planTemplate.name,
            blocklyXml,
        })

    render({
        className,
        documentPlans: {
            error:          getListError,
            loading:        getListLoading,
        },
    }, _, {
        openedDataFile,
        openedPlan,
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
                : getListLoading
                    ? <Loading className={ S.item } message="Loading document plans." />
                : getListError
                    ? <Error className={ S.item } message="Error loading document plans." />
                    : <OnboardCode onCreateXml={ this.onCreateXml } />
            }</div>
        );
    }
});
