import { h }                from 'preact';

import { composeQueries  }  from '../graphql';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { dictionaryIds } from '../graphql/queries.graphql';

import ItemRow              from './ItemRow';
import S                    from './Dictionary.sass';


export default composeQueries({
    dictionaryIds,
})(({
    dictionaryIds: {
        error,
        loading,
        dictionary,
    },
}) =>
    <table className={ S.className }>
        <thead>
            <tr>
                <th />
                <th>name</th>
                <th>
                    <LabelWithStatus
                        error={ error }
                        label="phrases"
                        loading={ loading }
                    />
                </th>
            </tr>
        </thead>
        <tbody>
            { dictionary &&
                dictionary.map(({ id }) =>
                    <ItemRow key={ id } id={ id } />
                )
            }
        </tbody>
    </table>
);
