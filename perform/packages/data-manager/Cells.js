import classnames           from 'classnames';
import { h }                from 'preact';

import CellBlock            from '../nlg-blocks/Cell';
import CellModifier         from '../nlg-blocks/Cell-modifier';
import DragInBlock          from '../drag-in-blocks/DragInBlock';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { QA }               from '../tests/constants';
import RowSelector          from '../row-selector/RowSelector';

import S                    from './Cells.sass';


export default ({ className, fileItem, fileStatus, onChangeRow, selectedRow }) =>
    <table className={ classnames( S.className, className, QA.DATA_MANAGER_CELL_TABLE ) }>
        <thead>
            <tr>
                <th className={ S.dragInBlock } />
                <th className={ S.dragInBlock } />
                <th className={ S.cellName }>Cell</th>
                <th>{
                    fileStatus.getDataError
                        ? <Error message={ fileStatus.getDataError } />
                    : fileStatus.getDataLoading
                        ? <Loading message="Loading cell values" />
                    : fileItem.data
                        ? <RowSelector
                            nextClassName={ QA.DATA_MANAGER_ROW_NEXT }
                            previousClassName={ QA.DATA_MANAGER_ROW_PREVIOUS }
                            onChange={ onChangeRow }
                            rows={ fileItem.data }
                            selectClassName={ QA.DATA_MANAGER_ROW_SELECT }
                            selected={ selectedRow }
                        />
                        : <Info message="Waiting for cell values" />
                }</th>
            </tr>
        </thead>
        <tbody>{ fileItem.fieldNames.map(( name, i ) =>
            <tr key={ i }>
                <td className={ S.dragInBlock }>
                    <DragInBlock
                        className={ QA.DATA_MANAGER_CELL_BLOCK }
                        color={ S.blockColor }
                        fields={{ name }}
                        type={ CellBlock.type }
                    />
                </td>
                <td className={ S.dragInBlock }>
                    <DragInBlock
                        className={ QA.DATA_MANAGER_CELL_BLOCK }
                        color={ S.modifierColor }
                        fields={{ name }}
                        type={ CellModifier.type }
                    />
                </td>
                <td className={ classnames( S.cellName, QA.DATA_MANAGER_CELL_NAME ) }>
                    { name }
                </td>
                <td className={ QA.DATA_MANAGER_CELL_VALUE }>{
                    fileItem.data
                    && fileItem.data[selectedRow]
                    && fileItem.data[selectedRow][name]
                }</td>
            </tr>
        )}</tbody>
    </table>;
