import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import { documentPlans }    from '../graphql/queries.graphql';

import Context              from './Context';


export default composeQueries({
    documentPlans,
})( class DocumentPlansContextProvider extends Component {

    value =                 {};

    render = ({
        children,
        documentPlans: {
            documentPlans,
            error,
            loading,
        },
    }) =>
        <Context.Provider
            children={ children }
            value={ Object.assign( this.value, {
                plans:      documentPlans,
                error,
                loading,
            }) }
        />;
});
