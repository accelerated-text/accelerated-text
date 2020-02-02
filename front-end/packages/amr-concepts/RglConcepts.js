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

import RglConceptRow        from './RglConceptRow';
import S                    from './AmrConcepts.sass';

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
                ? concepts.rgl.map( concept =>
                    <RglConceptRow key={ concept.id } concept={ concept } />
                )
                : <MessageTr>
                    <Error message="No concepts found. Please contact your system administrator." />
                </MessageTr>
            }
        </tbody>
    </table>
);