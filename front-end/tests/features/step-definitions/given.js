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
    const results = await this.page.$(".qa-tests-add-example-segment");

    expect(results).to.exists;
});