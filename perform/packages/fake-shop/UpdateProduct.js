import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';

import S                    from './UpdateProduct.sass';
import { updateProduct }    from './queries.graphql';


export default composeQueries({
    updateProduct,
})( class FakeShopUpdateProduct extends Component {

    static propTypes = {
        className:          PropTypes.string,
        description:        PropTypes.string.isRequired,
        disabled:           PropTypes.any,
        product:            PropTypes.object.isRequired,
        updateProduct:      PropTypes.func.isRequired,
    };

    state = {
        error:              null,
        isExpanded:         false,
        loading:            false,
    };

    onSubmit = () => {
        this.setState({
            loading:        true,
        });
        this.props.updateProduct({
            variables: {
                id:                 this.props.product.id,
                descriptionHtml:    this.props.description,
            },
        }).then(() => {
            this.setState({
                error:      null,
                loading:    false,
            });
        }).catch( error => {
            this.setState({
                error,
                loading:    false,
            });
        });
    };

    onClickExpand = () => {
        this.setState(({ isExpanded }) => ({
            isExpanded:     ! isExpanded,
        }));
    };

    render = (
        { className, description, disabled, product },
        { error, isExpanded, loading },
    ) =>
        <div className={ classnames( S.className, className ) }>
            <div className={ S.controls }>
                <button
                    children="Update product"
                    disabled={ disabled || loading || ! description }
                    onClick={ this.onSubmit }
                />
                <a
                    children={
                        isExpanded
                            ? 'hide current description'
                            : 'show current description'
                    }
                    className={ isExpanded ? S.isExpanded : '' }
                    onClick={ this.onClickExpand }
                />
            </div>
            { isExpanded &&
                <div
                    children={ product.descriptionHtml }
                    className={ S.productDescription }
                />
            }
        </div>;
});
