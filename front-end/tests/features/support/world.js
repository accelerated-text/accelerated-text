const { setWorldConstructor, World, setDefaultTimeout } = require('@cucumber/cucumber')
var puppeteer = require('puppeteer');

const ROOT = "http://localhost:8080";
const HEADLESS = process.env.HEADLESS !== "false";
setDefaultTimeout(30 * 1000);

class CustomWorld extends World {
    constructor(options) {
        super(options)
    }

    async openAcceleratedText() {
        this.browser = await puppeteer.launch({ headless: HEADLESS });
        this.page = await this.browser.newPage();
        await this.page.goto(ROOT);
    }

    async closeAcceleratedText() {
        await this.browser.close();
    }

    async openDocumentPlansView() {
        await this.page.goto(ROOT + "/");
    }

    async openAMRView() {
        await this.page.goto(ROOT + "/amr");
    }

    async openDLGView() {
        await this.page.goto(ROOT + "/dlg");
    }
}

setWorldConstructor(CustomWorld)