import { h, Component }     from 'preact';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;
const SAVE_AS =             `SAVE-AS-${ Math.random() }`;


export default class PlanSelectorList extends Component {

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.props.onClickNew()
        : ( evt.target.value === SAVE_AS )
            ? this.props.onClickSaveAs()
            : this.props.onChangeSelected( evt.target.value )

    render({ plans, selectedUid, uids }) {
        return (
            <select
                className={ S.className }
                onChange={ this.onChangeSelect }
                value={ selectedUid }
            >
                <option value={ ADD_NEW }>➕ New...</option>
                <option value={ SAVE_AS }>💾 Save as...</option>
                <optgroup label=" 📂 Open">
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
