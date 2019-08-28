import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import { listDataFiles }    from '../graphql/queries.graphql';

import Cells                from './Cells';
import Files                from './Files';
import S                    from './DataManager.sass';


export default composeQueries({
    listDataFiles,
})( class DataManager extends Component {

    onChangeRow = dataSampleRow =>
        this.context.E.documentPlans.onUpdate({
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
});
