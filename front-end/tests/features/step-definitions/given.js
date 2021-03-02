import { Given } from '@cucumber/cucumber';

Given("I am on {string} view", async function(view){
    console.log(this);
    switch(view) {
    case "DocumentPlan":
        return await this.openDocumentPlansView();
    case "AMR":
        return await this.openAMRView();
    case "DLG":
        return await this.openDLGView();
    }
});