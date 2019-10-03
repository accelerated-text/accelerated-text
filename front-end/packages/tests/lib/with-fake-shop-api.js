import withGraphql          from './with-graphql';


export default withGraphql({
    MACRO_NAME:             'fakeShopApi',
    URL:                    process.env.MOCK_SHOP_API_URL,
});
