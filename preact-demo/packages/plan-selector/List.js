import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Info                 from '../ui-messages/Info';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import S                    from './List.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;
const DEFAULT_NAME =        'Untitled Plan';


export default useStores([
    'planList',
])( class PlanSelectorList extends Component {

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.onClickNew()
            : this.props.E.planList.onSelectPlan( evt.target.value )

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', DEFAULT_NAME );
        planName && this.props.E.planList.onAddNew( planName );
    }

    render({
        planList: {
            getListLoading,
            getListError,
            plans,
            openedPlanUid,
        },
    }) {
        return (
            <div className={ S.className }>{
                !plans
                    ? (
                        getListLoading
                            ? <Info message="Loading plans." />
                        : getListError
                            ? <Error message="Loading error! Please refresh the page." />
                            : <UnexpectedWarning />
                    )
                : !plans.length
                    ? (
                        <button onClick={ this.onClickNew }>
                            ➕ New document plan
                        </button>
                    )
                    : (
                        <select
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
                    )
            }</div>
        );
    }
});
