import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';

import { createProduct }    from './queries.graphql';
import BookRecord           from './BookRecord.type';


export default composeQueries({
    createProduct,
})( class FakeShopAddProduct extends Component {

    static propTypes = {
        className:          PropTypes.string,
        description:        PropTypes.string.isRequired,
        disabled:           PropTypes.any,
        createProduct:      PropTypes.func.isRequired,
        record:             BookRecord.isRequired,
    };

    state = {
        error:              null,
        loading:            false,
    };

    onClick = () => {
        this.setState({
            loading:        true,
        });
        this.props.createProduct({
            variables: {
                descriptionHtml:    this.props.description,
                imageSrc:           this.props.record.thumbnail,
                sku:                this.props.record.id,
                title:              this.props.record.title,
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
        { className, description, disabled },
        { error, loading },
    ) =>
        <button
            children="Add new product"
            className={ className }
            disabled={ disabled || loading || ! description }
            onClick={ this.onClick }
        />;
});
