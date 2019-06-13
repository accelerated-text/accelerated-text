import ApolloClient         from 'apollo-boost';
import { ApolloProvider }   from 'react-apollo';
import gql                  from 'graphql-tag';
import { h }                from 'preact';
import { InMemoryCache }    from 'apollo-cache-inmemory';
import { mergeDeepRight }   from 'ramda';

import acceleratedTextResolvers from '../accelerated-text/local-state';
import resolvers            from './resolvers';
import typeDefs             from './types.graphql';


export { default as composeQueries }    from './compose-queries';


export const cache =        new InMemoryCache();

export const client = new ApolloClient({
    cache,
    credentials:            'omit',
    resolvers: mergeDeepRight(
        resolvers,
        acceleratedTextResolvers,
        /// dictionaryResolvers,
    ),
    typeDefs: gql`
        ${ typeDefs }
    `,
    uri:                    process.env.GRAPHQL_URL,
});

export const GraphQLProvider = props =>
    <ApolloProvider client={ client } { ...props } />;
