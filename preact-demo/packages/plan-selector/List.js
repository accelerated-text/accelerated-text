import { h, Component }     from 'preact';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;


export default class PlanSelectorList extends Component {

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.props.onClickNew()
            : this.props.onChangeSelected( evt.target.value )

    render({ plans, selectedUid, uids }) {
        return (
            <select
                className={ S.className }
                onChange={ this.onChangeSelect }
                value={ selectedUid }
            >
                <option value={ ADD_NEW }>➕ New...</option>
                <optgroup label="📂 Open a plan">
                    { uids.map( uid =>
                        <option key={ uid } value={ uid }>
                            📄 { plans[uid].name }
                        </option>
                    )}
                </optgroup>
            </select>
        );
    }
}
