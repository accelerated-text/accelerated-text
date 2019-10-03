import ApolloClient         from 'apollo-boost';
import {
    ApolloProvider,
    withApollo,
}                           from 'react-apollo';
import { h }                from 'preact';
import { InMemoryCache }    from 'apollo-cache-inmemory';

import cacheRedirects       from './cache-redirects';
import typeDefs             from './schema.graphql';


export { default as composeQueries }    from './compose-queries';
export { Query }            from 'react-apollo';


export const cache =        new InMemoryCache({
    cacheRedirects,
});

export const client = new ApolloClient({
    cache,
    credentials:            'omit',
    typeDefs,
    uri:                    process.env.ACC_TEXT_GRAPHQL_URL,
});

export const GraphQLProvider = props =>
    <ApolloProvider client={ client } { ...props } />;


export const withClient =   withApollo;
