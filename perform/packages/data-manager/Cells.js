import classnames           from 'classnames';
import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import RowSelector          from '../row-selector/RowSelector';

import S                    from './Cells.sass';


export default ({ className, fileItem, fileStatus, onChangeRow, selectedRow }) =>
    <table className={ classnames( S.className, className ) }>
        <thead>
            <tr>
                <th className={ S.dragInBlock } />
                <th className={ S.cellName }>Cell</th>
                <th>{
                    fileStatus.getDataError
                        ? <Error message={ fileStatus.getDataError } />
                    : fileStatus.getDataLoading
                        ? <Loading message="Loading cell values" />
                    : fileItem.data
                        ? <RowSelector
                            onChange={ onChangeRow }
                            rows={ fileItem.data }
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
                        color={ S.dragInColor }
                        fields={{ name }}
                        type="Cell"
                        width={ 36 }
                    />
                </td>
                <td className={ S.cellName }>{ name }</td>
                <td>{
                    fileItem.data
                    && fileItem.data[0]
                    && fileItem.data[0][name]
                }</td>
            </tr>
        )}</tbody>
    </table>;
