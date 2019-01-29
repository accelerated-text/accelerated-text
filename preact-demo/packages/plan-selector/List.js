import { h, Component }     from 'preact';

import { useStores }        from '../vesa/';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;


export default useStores([
    'planList',
])( class PlanSelectorList extends Component {

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.props.onClickNew()
            : this.props.E.planList.onSelectPlan( evt.target.value )

    render({
        planList: {
            plans,
            openedPlanUid,
        },
    }) {
        return (
            <select
                className={ S.className }
                onChange={ this.onChangeSelect }
                value={ openedPlanUid }
            >
                <option value={ ADD_NEW }>➕ New...</option>
                <optgroup label="📂 Open a plan">
                    { plans.map( plan =>
                        <option value={ plan.uid }>
                            📄 { plan.name }
                        </option>
                    )}
                </optgroup>
            </select>
        );
    }
});
