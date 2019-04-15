import { h }                from 'preact';

import DragInBlock          from '../drag-in-blocks/DragInBlock';

import S                    from './Cells.sass';


const ROWS = [
    'Nike1',
    'comfort',
    'support',
    'and the list',
    'goes on',
];


export default ({ planFile }) =>
    <table className={ S.className }>
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
                <td>{ ROWS[i] }</td>
            </tr>
        )}</tbody>
    </table>;
