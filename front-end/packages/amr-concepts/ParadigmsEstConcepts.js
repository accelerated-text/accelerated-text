import { h }                from 'preact';

import { composeQueries  }  from '../graphql';
import { Error, Loading }   from '../ui-messages/';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { concepts }         from '../graphql/queries.graphql';

import ConceptRow           from './RglConceptRow';
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
                ? concepts.ParadigmsEst.map( concept =>
                    <ConceptRow key={ concept.id } concept={ concept } />
                )
                : <MessageTr>
                    <Error message="No concepts found. Please contact your system administrator." />
                </MessageTr>
            }
        </tbody>
    </table>
);
