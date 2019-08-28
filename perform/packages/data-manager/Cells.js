import classnames           from 'classnames';
import { h }                from 'preact';
import { pathOr }           from 'ramda';

import CellBlock            from '../nlg-blocks/Cell';
import CellModifier         from '../nlg-blocks/Cell-modifier';
import { composeQueries }   from '../graphql/';
import DragInBlock          from '../drag-in-blocks/DragInBlock';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { getDataFile }      from '../graphql/queries.graphql';
import { QA }               from '../tests/constants';
import RowSelector          from '../row-selector/RowSelector';

import S                    from './Cells.sass';


export default composeQueries({
    getDataFile:    [ getDataFile, { id: 'id' }],
})(({
    className,
    getDataFile: {
        error,
        getDataFile,
        loading,
    },
    onChangeRow,
    selectedRow,
}) => {

    const valueDict =
        Object.fromEntries(
            pathOr([], [ 'records', selectedRow, 'fields' ], getDataFile )
                .map( field => [ field.fieldName, field.value ])
        );

    return (
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
                        : getDataFile && getDataFile.records
                            ? <RowSelector
                                nextClassName={ QA.DATA_MANAGER_ROW_NEXT }
                                previousClassName={ QA.DATA_MANAGER_ROW_PREVIOUS }
                                onChange={ onChangeRow }
                                rows={ getDataFile.records }
                                selectClassName={ classnames( S.selectRow, QA.DATA_MANAGER_ROW_SELECT ) }
                                selected={ selectedRow }
                            />
                            : <Info message="Waiting for cell values" />
                    }</th>
                </tr>
            </thead>
            <tbody>{ getDataFile && getDataFile.fieldNames.map(( name, i ) =>
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
    );
});
