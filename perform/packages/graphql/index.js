import ApolloClient         from 'apollo-boost';
import { ApolloProvider }   from 'react-apollo';
import gql                  from 'graphql-tag';
import { h }                from 'preact';
import { InMemoryCache }    from 'apollo-cache-inmemory';
import { mergeDeepRight }   from 'ramda';

import dictionaryResolvers  from '../dictionary/resolvers';
import dictionaryTypes      from '../dictionary/types.graphql';

import resolvers            from './resolvers';
import typeDefs             from './types.graphql';


export { default as composeQueries }    from './compose-queries';
export { default as gql }               from 'graphql-tag';
export {
    Mutation as GqlMutation,
    Query as GqlQuery,
}   from 'react-apollo';


export const cache =        new InMemoryCache();

export const client = new ApolloClient({
    cache,
    credentials:            'omit',
    resolvers: mergeDeepRight(
        resolvers,
        dictionaryResolvers,
    ),
    typeDefs: gql`
        ${ typeDefs }
        ${ dictionaryTypes }
    `,
    uri:                    process.env.GRAPHQL_URL,
});

export const GqlProvider = props =>
    <ApolloProvider client={ client } { ...props } />;
