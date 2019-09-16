import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { listDataFiles }    from '../graphql/queries.graphql';
import PlanActions          from '../document-plans/Actions';

import Cells                from './Cells';
import Files                from './Files';
import S                    from './DataManager.sass';


export default PlanActions( composeQueries({
    listDataFiles,
})( class DataManager extends Component {

    static propTypes = {
        listDataFiles:      PropTypes.object.isRequired,
        onUpdatePlan:       PropTypes.func.isRequired,
        plan:               PropTypes.object,
    };

    onChangeRow = dataSampleRow =>
        this.props.onUpdatePlan({
            ...this.props.plan,
            dataSampleRow,
        });

    render({
        listDataFiles: {
            error,
            listDataFiles,
            loading,
        },
        plan,
    }) {
        return (
            <div className={ S.className }>
                <Files
                    className={ S.files }
                    error={ error }
                    fileCount={ listDataFiles && listDataFiles.dataFiles && listDataFiles.dataFiles.length || 0 }
                    loading={ loading }
                    plan={ plan }
                />
                { plan && plan.dataSampleId &&
                    <Cells
                        className={ S.cells }
                        id={ plan.dataSampleId }
                        onChangeRow={ this.onChangeRow }
                        selectedRow={ plan.dataSampleRow }
                    />
                }
            </div>
        );
    }
}));
