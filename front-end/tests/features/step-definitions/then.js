import { Then } from '@cucumber/cucumber';
const { assert, expect } = require('chai')

Then("{string} appears in workspace", async function(title){
    const results = await this.page.$x(`//text[contains(@class, 'blocklyText') and text() = '${title}']`);

    expect(results).to.exists;
})