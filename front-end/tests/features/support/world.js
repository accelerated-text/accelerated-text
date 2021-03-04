const { setWorldConstructor, World, setDefaultTimeout } = require('@cucumber/cucumber')
var puppeteer = require('puppeteer');

const ROOT = process.env.FRONTEND_URL || "http://localhost:8080";
setDefaultTimeout(30 * 1000);

class CustomWorld extends World {
    constructor(options) {
        super(options)
    }

    async openAcceleratedText() {
        this.browser = await puppeteer.launch({headless: true, args: ['--no-sandbox', '--disable-setuid-sandbox']});
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