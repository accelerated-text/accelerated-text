import classnames           from 'classnames';
import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';

import S                    from './Cells.sass';


export default ({ className, planFile }) =>
    <table className={ classnames( S.className, className ) }>
        <thead>
            <tr>
                <th className={ S.dragInBlock } />
                <th className={ S.cellName }>Cell</th>
                <th>
                    <button>◀️</button>
                    <select>
                        <option>1</option>
                        <option>2</option>
                        <option>3</option>
                    </select>
                    <button>▶️</button>
                </th>
            </tr>
        </thead>
        <tbody>{ planFile.fieldNames.map(( name, i ) =>
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
                    planFile.data
                    && planFile.data[0]
                    && planFile.data[0][name]
                }</td>
            </tr>
        )}</tbody>
    </table>;
