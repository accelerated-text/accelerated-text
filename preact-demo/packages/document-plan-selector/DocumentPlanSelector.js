import { h, Component }     from 'preact';

import { mount, useStores } from '../vesa/';

import documentPlanSelector from './store';
import S                    from './DocumentPlanSelector.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;


export default mount({
    documentPlanSelector,
})( useStores([
    'documentPlanSelector',
])( class DocumentPlanSelector extends Component {

    onChange = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.onClickNew()
            : this.props.E.documentPlanSelector.onSelectPlan( evt.target.value )

    onClickEdit = evt => {
        const {
            documentPlanSelector: {
                plans,
                selectedPlan,
            },
        } = this.props;
        const plan =        plans.find( plan => plan.id === selectedPlan );

        if( !plan ) {
            return;
        }

        const planName =    window.prompt( 'Rename Document Plan:', plan.name );
        return this.props.E.documentPlanSelector.onRenamePlan({
            id:             selectedPlan,
            name:           planName,
        });
    }

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', 'Untitled Plan' );
        planName && this.props.E.documentPlanSelector.onAddNew( planName );
    }

    onClickRemove = evt => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
        && this.props.E.documentPlanSelector.onRemoveSelected()
    );

    render({
        documentPlanSelector: {
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
                            <optgroup label="📂 Select a plan">
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
}));
