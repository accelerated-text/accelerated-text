export default page =>
    page.on( 'framenavigated',
        frame => frame.evaluate(
            () => window.__APOLLO_DEVTOOLS_GLOBAL_HOOK__ = null // eslint-disable-line no-underscore-dangle
        ),
    );
