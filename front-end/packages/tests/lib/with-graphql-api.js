import withGraphql          from './with-graphql';


export default withGraphql({
    MACRO_NAME:             'graphqlApi',
    URL:                    process.env.ACC_TEXT_GRAPHQL_URL,
});
