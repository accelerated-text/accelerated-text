import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';

import S                    from './ItemControls.sass';


export default class PlanSelectorItemControls extends Component {

    onClickEdit = () => {
        const {
            onUpdate,
            plan,
        } = this.props;

        const name =    window.prompt( 'Rename Document Plan:', plan.name );
        if( name ) {
            onUpdate({ ...plan, name });
        }
    }

    onClickRemove = () => {
        if( window.confirm( '⚠️ Are you sure you want to remove this plan?' )) {
            this.props.onDelete( this.props.plan );
        }
    }

    renderError( err ) {
        return (
            err
                ? <Error className={ S.icon } justIcon message={ err.toString() } />
                : null
        );
    }

    render({ plan, status }) {
        return (
            <div className={ S.className }>{
                ( !plan || !status || status.isDeleted )
                    ? null
                : status.createLoading
                    ? [
                        this.renderError( status.createError ),
                        <Loading className={ S.icon } justIcon message="Saving." />,
                    ]
                : status.deleteLoading
                    ? [
                        this.renderError( status.deleteError ),
                        <Loading className={ S.icon } justIcon message="Removing." />,
                    ]
                    : [
                        this.renderError( status.updateError ),
                        this.renderError( status.deleteError ),
                        ( status.updateLoading
                            ? <Loading className={ S.icon } justIcon message="Saving." />
                            : <button onClick={ this.onClickEdit }>📝</button>
                        ),
                        <button onClick={ this.onClickRemove }>🗑️</button>,
                    ]
            }</div>
        );
    }
}
