import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import { useStores }        from '../vesa/';

import S                    from './ItemControls.sass';


export default useStores([
    'planList',
])( class PlanSelectorItemControls extends Component {

    onClickEdit = evt => {
        const {
            E,
            item,
        } = this.props;

        if( !item ) {
            return E.planList.onGetList();
        }

        const name =    window.prompt( 'Rename Document Plan:', item.name );
        if( !name ) {
            return;
        }

        return E.planList.onRenamePlan({ item, name });
    }

    onClickRemove = () => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
            && this.props.E.planList.onRemovePlan( this.props.item )
    )

    renderError( err ) {
        return (
            err
                ? <Error className={ S.icon } justIcon message={ err.toString() } />
                : null
        );
    }

    render({ status }) {

        return (
            <div className={ S.className }>{
                status.addLoading
                    ? [
                        this.renderError( status.addError ),
                        <Loading className={ S.icon } justIcon message="Saving." />,
                    ]
                : status.removeLoading
                    ? [
                        this.renderError( status.removeError ),
                        <Loading className={ S.icon } justIcon message="Removing." />,
                    ]
                    : [
                        this.renderError( status.renameError ),
                        this.renderError( status.removeError ),
                        ( status.renameLoading
                            ? <Loading className={ S.icon } justIcon message="Saving." />
                            : <button onClick={ this.onClickEdit }>📝</button>
                        ),
                        <button onClick={ this.onClickRemove }>🗑️</button>,
                    ]
            }</div>
        );
    }
});
