import ApolloClient         from 'apollo-boost';
import { ApolloProvider }   from 'react-apollo';
import { h }                from 'preact';
import { InMemoryCache }    from 'apollo-cache-inmemory';


export const cache =        new InMemoryCache;

export const client = new ApolloClient({
    cache,
    credentials:                    'omit',
    uri:                            process.env.FAKE_SHOP_API_URL,
});

export const GraphQLProvider = props =>
    <ApolloProvider client={ client } { ...props } />;
