import ApolloClient         from 'apollo-boost';
import gqlTag               from 'graphql-tag';


export const gql =          gqlTag;

export const gqlClient = new ApolloClient({
    credentials:    'omit',
    uri:            process.env.GRAPHQL_URL,
});
