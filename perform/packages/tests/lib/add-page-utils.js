const SELECTOR_WAIT_OPTIONS = {
    timeout:            1e3,
};


export default ( t, run, ...args ) =>
    run(
        Object.assign( t, {
            findElement: ( selector, page = t.page ) =>
                t.notThrowsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS )),
            notFindElement: ( selector, page = t.page ) =>
                t.throwsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS )),
        }),
        ...args,
    );
