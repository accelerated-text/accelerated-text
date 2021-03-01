import { h }                from 'preact';
import {
    compose,
    prop,
    sortBy,
    toLower,
}                           from 'ramda';

import { composeQueries  }  from '../graphql';
import { Error, Loading, Info }   from '../ui-messages/';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { concepts }         from '../graphql/queries.graphql';

import ConceptRow           from './ConceptRow';
import S                    from './AmrConcepts.sass';


const sortByLabel =         sortBy( compose( toLower, prop( 'label' )));

const MessageTr = ({ children }) =>
    <tr><td colspan="3">{ children }</td></tr>;


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
            { error
                ? <MessageTr>
                    <Error message={ error } />
                </MessageTr>
            : loading
                ? <MessageTr>
                    <Loading />
                </MessageTr>
            : (concepts && Object.keys(concepts).length > 0)
                ? sortByLabel( concepts.amr ).map( concept =>
                    <ConceptRow key={ concept.id } concept={ concept } />
                )
                : <MessageTr>
                    <Info message="No concepts found." />
                </MessageTr>
            }
        </tbody>
    </table>
);
