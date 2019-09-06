import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;
const SAVE_AS =             `SAVE-AS-${ Math.random() }`;


export default class PlanSelectorList extends Component {

    static propTypes = {
        onChangeSelected:   PropTypes.func.isRequired,
        onClickNew:         PropTypes.func.isRequired,
        onClickSaveAs:      PropTypes.func.isRequired,
        openedPlan:         PropTypes.object,
        plans:              PropTypes.array,
    };

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.props.onClickNew()
        : ( evt.target.value === SAVE_AS )
            ? this.props.onClickSaveAs()
            : this.props.onChangeSelected( evt.target.value )

    render({ plans, openedPlan }) {
        return (
            <select
                className={ S.className }
                onChange={ this.onChangeSelect }
                value={ openedPlan.uid }
            >
                <option value={ ADD_NEW }>➕ New...</option>
                <option value={ SAVE_AS }>💾 Save as...</option>
                <optgroup label=" 📂 Open">
                    { openedPlan && ! openedPlan.id &&
                        <option key={ openedPlan.uid } value={ openedPlan.uid }>
                            📑 { openedPlan.name }
                        </option>
                    }
                    { plans && plans.map( plan =>
                        <option key={ plan.uid } value={ plan.uid }>
                            📄 { plan.name }
                        </option>
                    )}
                </optgroup>
            </select>
        );
    }
}
