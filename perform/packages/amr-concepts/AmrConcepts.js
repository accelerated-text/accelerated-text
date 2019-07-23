import { h }                from 'preact';
import {
    compose,
    prop,
    sortBy,
    toLower,
}                           from 'ramda';

import { composeQueries  }  from '../graphql';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { concepts }         from '../graphql/queries.graphql';

import ConceptRow           from './ConceptRow';
import S                    from './AmrConcepts.sass';


const sortByLabel =         sortBy( compose( toLower, prop( 'label' )));


export default composeQueries({
    concepts,
})(({
    concepts: {
        concepts,
        error,
        loading,
    },
}) =>
    <table className={ S.className }>
        <thead>
            <tr>
                <th className={ S.block } />
                <th className={ S.name } >name</th>
                <th className={ S.about } >
                    <LabelWithStatus
                        error={ error }
                        label="about"
                        loading={ loading }
                    />
                </th>
            </tr>
        </thead>
        <tbody>
            { concepts && sortByLabel( concepts.concepts ).map( concept =>
                <ConceptRow key={ concept.id } concept={ concept } />
            )}
        </tbody>
    </table>
);
