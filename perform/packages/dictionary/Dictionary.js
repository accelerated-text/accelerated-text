import { h }                from 'preact';

import { gql, GqlQuery }    from '../graphql';
import LabelWithStatus      from '../label-with-status/LabelWithStatus';

import DictionaryItemRow    from './DictionaryItemRow';
import S                    from './Dictionary.sass';


const query = gql`{
    orgDictionary @client {
        name
        usageModels {
            phrase { id text }
            defaultUsage {
                flag { id name }
                usage
            }
            readerUsage {
                flag { id name }
                usage
            }
        }
    }
}`;


export default () =>
    <GqlQuery query={ query }>{
        ({ data, error, loading }) => (
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
                    { data && data.orgDictionary &&
                        data.orgDictionary.map( item =>
                            <DictionaryItemRow key={ item.id } item={ item } />
                        )
                    }
                </tbody>
            </table>
        )
    }</GqlQuery>;
