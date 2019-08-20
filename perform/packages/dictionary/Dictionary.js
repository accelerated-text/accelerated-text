import { h }                from 'preact';

import { composeQueries  }  from '../graphql';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';
import { dictionary }       from '../graphql/queries.graphql';

import AddItem              from './AddItem';
import ItemRow              from './ItemRow';
import S                    from './Dictionary.sass';


export default composeQueries({
    dictionary,
})(({
    dictionary: {
        dictionary,
        error,
        loading,
    },
}) =>
    <table className={ S.className }>
        <thead>
            <tr>
                <th className={ S.block } />
                <th className={ S.block } />
                <th className={ S.name }>name</th>
                <th>
                    <LabelWithStatus
                        error={ error }
                        label="phrases"
                        loading={ loading }
                    />
                </th>
            </tr>
        </thead>
        <tbody className={ S.items }>
            { dictionary && dictionary.items.map( item =>
                <ItemRow key={ item.id } item={ item } />
            )}
        </tbody>
        <tbody>
            <tr>
                <td colspan="4">
                    <AddItem refetchQueries={ [ 'dictionary' ] } />
                </td>
            </tr>
        </tbody>
    </table>
);
