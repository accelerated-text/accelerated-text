import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { sortByCreatedAt }  from '../document-plans/functions';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;
const SAVE_AS =             `SAVE-AS-${ Math.random() }`;


export default class PlanSelectorList extends Component {

    static propTypes = {
        onChangeSelected:   PropTypes.func.isRequired,
        onClickNew:         PropTypes.func.isRequired,
        onClickSaveAs:      PropTypes.func.isRequired,
        selectedPlan:       PropTypes.object,
        plans:              PropTypes.array,
    };

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.props.onClickNew()
        : ( evt.target.value === SAVE_AS )
            ? this.props.onClickSaveAs()
            : this.props.onChangeSelected( evt.target.value )

    render({ plans, selectedPlan }) {
        return (
            <select
                className={ S.className }
                onChange={ this.onChangeSelect }
                value={ selectedPlan.uid }
            >
                <option value={ ADD_NEW }>➕ New...</option>
                <option value={ SAVE_AS }>💾 Save as...</option>
                <optgroup label=" 📂 Open">
                    { selectedPlan && ! selectedPlan.id &&
                        <option key={ selectedPlan.uid } value={ selectedPlan.uid }>
                            📑 { selectedPlan.name }
                        </option>
                    }
                    { plans && plans.sort( sortByCreatedAt ).map( plan =>
                        <option key={ plan.uid } value={ plan.uid }>
                            📄 { plan.name }
                        </option>
                    )}
                </optgroup>
            </select>
        );
    }
}
