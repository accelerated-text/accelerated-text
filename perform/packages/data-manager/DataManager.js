import { h, Component }     from 'preact';

import {
    findFileByPlan,
    getStatus,
}   from '../data-samples/functions';
import { useStores }        from '../vesa/';

import Cells                from './Cells';
import Files                from './Files';
import S                    from './DataManager.sass';


export default useStores([
    'dataSamples',
])( class DataManager extends Component {

    onChangeRow = dataSampleRow =>
        this.props.E.documentPlans.onUpdate({
            ...this.props.plan,
            dataSampleRow,
        });

    render({ dataSamples, plan }) {

        const fileItem =        findFileByPlan( dataSamples, plan );
        const fileStatus =      fileItem && getStatus( dataSamples, fileItem );

        return (
            <div className={ S.className }>
                <Files className={ S.files } plan={ plan } />
                { fileItem && fileItem.fieldNames &&
                    <Cells
                        className={ S.cells }
                        fileItem={ fileItem }
                        fileStatus={ fileStatus }
                        onChangeRow={ this.onChangeRow }
                        selectedRow={ plan.dataSampleRow }
                    />
                }
            </div>
        );
    }
});
