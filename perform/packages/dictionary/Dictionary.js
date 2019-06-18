import { h }                from 'preact';

import { composeQueries  }  from '../graphql';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { dictionary }       from '../graphql/queries.graphql';

import ItemRow              from './ItemRow';
import S                    from './Dictionary.sass';


export default composeQueries({
    dictionary,
})(({
    dictionary: {
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
            { dictionary && dictionary.items.map( item =>
                <ItemRow key={ item.id } item={ item } />
            )}
        </tbody>
    </table>
);
