import classnames           from 'classnames';
import { h }                from 'preact';
import { pathOr }           from 'ramda';

import CellBlock            from '../nlg-blocks/Cell';
import CellModifier         from '../nlg-blocks/Cell-modifier';
import { composeQueries }   from '../graphql/';
import { dataFieldsToObj }  from '../data-samples/functions';
import DragInBlock          from '../drag-in-blocks/DragInBlock';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { getRelevantSamples }      from '../graphql/queries.graphql';
import { QA }               from '../tests/constants';
import RowSelector          from '../row-selector/RowSelector';

import S                    from './Cells.sass';
import SampleAlgorithm      from './SampleAlgorithm';


export default composeQueries({
    getRelevantSamples:    [ getRelevantSamples, { id: 'id', method: 'method' }],
})(({
    className,
    getRelevantSamples: {
        error,
        getRelevantSamples,
        loading,
    },
    onChangeRow,
    selectedRow,
    onChangeMethod,
}) => {
    const valueDict = dataFieldsToObj(
        pathOr([], [ 'records', selectedRow, 'fields' ], getRelevantSamples ),
    );

    return (
    <div>
        <SampleAlgorithm
            onChange={ onChangeMethod }
        />
        <table className={ classnames( S.className, className, QA.DATA_MANAGER_CELL_TABLE ) }>
            <thead>
                <tr>
                    <th className={ S.dragInBlock } />
                    <th className={ S.dragInBlock } />
                    <th className={ S.cellName }>Cell</th>
                    <th className={ S.cellValue }>{
                        error
                            ? <Error message={ error } />
                        : loading
                            ? <Loading message="Loading cell values" />
                        : getRelevantSamples && getRelevantSamples.records
                            ? <RowSelector
                                nextClassName={ QA.DATA_MANAGER_ROW_NEXT }
                                previousClassName={ QA.DATA_MANAGER_ROW_PREVIOUS }
                                onChange={ onChangeRow }
                                rows={ getRelevantSamples.records }
                                selectClassName={ classnames( S.selectRow, QA.DATA_MANAGER_ROW_SELECT ) }
                                selected={ selectedRow }
                            />
                            : <Info message="Waiting for cell values" />
                    }</th>
                </tr>
            </thead>
            <tbody>{ getRelevantSamples && getRelevantSamples.fieldNames.map(( name, i ) =>
                <tr key={ i }>
                    <td className={ S.dragInBlock }>
                        <DragInBlock
                            block={ CellBlock }
                            className={ QA.DATA_MANAGER_CELL_BLOCK }
                            fields={{ name }}
                        />
                    </td>
                    <td className={ S.dragInBlock }>
                        <DragInBlock
                            block={ CellModifier }
                            className={ QA.DATA_MANAGER_CELL_BLOCK }
                            fields={{ name }}
                        />
                    </td>
                    <td className={ classnames( S.cellName, QA.DATA_MANAGER_CELL_NAME ) }>
                        { name }
                    </td>
                    <td className={ classnames( S.cellValue, QA.DATA_MANAGER_CELL_VALUE ) }>
                        { valueDict[name] }
                    </td>
                </tr>
            )}</tbody>
        </table>
     </div>
    );
});
