import { compose, graphql }     from 'react-apollo';
import { mapObjIndexed, path }  from 'ramda';


export default queries => {

    const propsToVars = varProp => ( varProp && {
        options: props => ({
            variables: mapObjIndexed(
                propName => (
                    propName instanceof Array
                        ? path( propName, props )
                        : props[propName]
                ),
                varProp,
            ),
        }),
    });
    
    return compose(
        ...Object.keys( queries )
            .map( name =>
                queries[name] instanceof Array
                    ? graphql( queries[name][0], {
                        name,
                        ...propsToVars( queries[name][1]),
                        ...queries[name][2],
                    })
                    : graphql( queries[name], {
                        name,
                    })
            )
    );
};

