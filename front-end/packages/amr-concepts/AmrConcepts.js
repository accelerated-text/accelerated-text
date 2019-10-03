import { h }                from 'preact';
import {
    compose,
    prop,
    sortBy,
    toLower,
}                           from 'ramda';

import { composeQueries  }  from '../graphql';
import { Error, Loading }   from '../ui-messages/';
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
            : concepts
                ? sortByLabel( concepts.concepts ).map( concept =>
                    <ConceptRow key={ concept.id } concept={ concept } />
                )
                : <MessageTr>
                    <Error message="No AMR Concepts found. Please contact your system administrator." />
                </MessageTr>
            }
        </tbody>
    </table>
);
