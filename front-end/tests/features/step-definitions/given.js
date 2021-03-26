import { Given } from '@cucumber/cucumber';
const { assert, expect } = require('chai')

Given("I am on {string} view", async function(view){
    switch(view) {
    case "DocumentPlan":
        return await this.openDocumentPlansView();
    case "AMR":
        return await this.openAMRView();
    case "DLG":
        return await this.openDLGView();
    }
});

Given("Workspace is empty", async function(){
    const results = await this.page.waitForSelector(".qa-tests-add-example-segment");

    expect(results).to.exists;
});

Given("Workspace is without errors", async function(){
    await this.page.screenshot({ path: './output/image.jpg', type: 'jpeg' })
    const results = await this.page.$(".qa-tests-ui-error");

    expect(results).to.not.exists;
});