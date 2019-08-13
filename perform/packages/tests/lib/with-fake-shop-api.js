import withGraphql          from './with-graphql';


export default withGraphql({
    MACRO_NAME:             'fakeShopApi',
    URL:                    process.env.FAKE_SHOP_API_URL,
});
