import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';

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
        loading:            false,
    };

    onClick = () => {
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

    render = (
        { className, description, disabled, product },
        { error, loading },
    ) =>
        <button
            children="Update the product"
            className={ className }
            disabled={ disabled || loading || ! description }
            onClick={ this.onClick }
        />;
});
