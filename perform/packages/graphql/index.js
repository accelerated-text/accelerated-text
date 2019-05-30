import ApolloClient         from 'apollo-boost';
import {
    ApolloProvider,
    Query,
}   from 'react-apollo';
import gqlTag               from 'graphql-tag';
import { h }                from 'preact';
import { InMemoryCache }    from 'apollo-cache-inmemory';

import resolvers            from './resolvers';
import typeDefs             from './types.graphql';


export const gqlCache =     new InMemoryCache();


export const gql =          gqlTag;

export const gqlClient = new ApolloClient({
    cache:          gqlCache,
    credentials:    'omit',
    resolvers,
    typeDefs,
    uri:            process.env.GRAPHQL_URL,
});

export const GqlProvider = props =>
    <ApolloProvider client={ gqlClient } { ...props } />;

export const GqlQuery =     Query;
