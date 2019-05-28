import ApolloClient         from 'apollo-boost';
import {
    ApolloProvider,
    Query,
}   from 'react-apollo';
import gqlTag               from 'graphql-tag';
import { h }                from 'preact';


export const gql =          gqlTag;

export const gqlClient = new ApolloClient({
    credentials:    'omit',
    uri:            process.env.GRAPHQL_URL,
});

export const GqlProvider = props =>
    <ApolloProvider client={ gqlClient } { ...props } />;

export const GqlQuery =     Query;
