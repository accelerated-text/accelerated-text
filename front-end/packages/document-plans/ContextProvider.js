import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import { documentPlans, rglPlans, amrPlans }    from '../graphql/queries.graphql';

import Context              from './Context';


const DPContextProvider = composeQueries({
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



const RGLContextProvider = composeQueries({
    rglPlans,
})( class DocumentPlansContextProvider extends Component {

    value =                 {};

    render = ({
        children,
        rglPlans: {
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

const AMRContextProvider = composeQueries({
    amrPlans,
})( class DocumentPlansContextProvider extends Component {

    value =                 {};

    render = ({
        children,
        amrPlans: {
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

export { DPContextProvider, RGLContextProvider, AMRContextProvider };
