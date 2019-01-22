import { h, Component }     from 'preact';

import { useStores }    from '../vesa/';

import S                from './PlanSelector.sass';


const ADD_NEW =         `ADD-NEW-${ Math.random() }`;


export default useStores([
    'planSelector',
])( class PlanSelector extends Component {

    onChange = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.onClickNew()
            : this.props.E.planSelector.onSelectPlan( evt.target.value )

    onClickEdit = evt => {
        const {
            planSelector: {
                plans,
                selectedPlan,
            },
        } = this.props;
        const plan =        plans.find( plan => plan.id === selectedPlan );

        if( !plan ) {
            return;
        }

        const planName =    window.prompt( 'Rename Document Plan:', plan.name );
        return this.props.E.planSelector.onRenamePlan({
            id:             selectedPlan,
            name:           planName,
        });
    }

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', 'Untitled Plan' );
        planName && this.props.E.planSelector.onAddNew( planName );
    }

    onClickRemove = evt => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
        && this.props.E.planSelector.onRemoveSelected()
    );

    render({
        planSelector: {
            plans,
            selectedPlan,
        }}) {
        return (
            <div className={ S.className }>
                { plans.length
                    ? (
                        <select
                            onChange={ this.onChange }
                            value={ selectedPlan }
                        >
                            <optgroup label="📂 Open a plan">
                                { plans.map( plan => (
                                    <option value={ plan.id }>
                                        📄 { plan.name }
                                    </option>
                                ))}
                            </optgroup>
                            <option value={ ADD_NEW }>➕ New...</option>
                        </select>
                    )
                    : null
                }
                { selectedPlan
                    ? [
                        <button onClick={ this.onClickEdit }>📝</button>,
                        <button onClick={ this.onClickRemove }>❌</button>,
                    ]
                    : (
                        <button onClick={ this.onClickNew }>
                            ➕ New document plan
                        </button>
                    )
                }
            </div>
        );
    }
});
