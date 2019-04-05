const SELECTOR_WAIT_OPTIONS = {
    timeout:            1e3,
};


export default t => {

    t.findElement = ( page, selector ) =>
        t.notThrowsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS ));

    t.notFindElement = ( page, selector ) =>
        t.throwsAsync( page.waitForSelector( selector, SELECTOR_WAIT_OPTIONS ));

    return t;
};
